#
# .SYNOPSIS
# Returns true if the ComputerName parameter represents the local machine.
#
function Test-LocalComputer
{
	[CmdletBinding()]
	param
	(
		[Parameter( Mandatory = $true, Position = 0, ValueFromPipeline = $true )]
		[string] $ComputerName
	)

	$ComputerName -ieq $env:ComputerName -or $ComputerName -ieq "localhost" -or $ComputerName -ieq "127.0.0.1"
	return
}

#
# .SYNOPSIS
# Throws an exception if this process is not elevated 32 bit powershell.
#
function Test-LocalPowershellContext
{
	$principal = New-Object System.Security.Principal.WindowsPrincipal( [System.Security.Principal.WindowsIdentity]::GetCurrent() )
	if( $principal.IsInRole( [System.Security.Principal.WindowsBuiltInRole]::Administrator ) -eq $false )
	{
		throw "The current powershell process is not elevated. If this script is executing on the local computer it must be run under elevated 32 bit powershell."
	}

	if( [Environment]::Is64BitProcess )
	{
		throw "The current powershell process is not 32 bit. If this script is executing on the local computer it must be run under elevated 32 bit powershell."
	}
}

#
# .SYNOPSIS
# Invokes a script on the machine specified by $ComputerName. If it's the local machine
# then the script is run directly, otherwise we use Invoke-Command to remote using 32 bit
# powershell and CredSSP authentication.
#
# .NOTES
# If this is the local machine then the script is run directly, otherwise we use Invoke-Command
# with 32 bit powershell and CredSSP authentication. If remoting is performed, the local script
# will also be copied to the remote machine before running it for a few reasons:
# - To produce better stack traces with real file names.
# - So that $PSScriptRoot and other script-based variables can be used in the scripts.
# - So other scripts can be called using relative paths on the remote machine.
#
function Invoke-ScriptOnComputer
{
	[CmdletBinding()]
	param
	(
		[Parameter( Mandatory = $true, Position = 0 )]
		[string] $ScriptPath,

		[Parameter( Mandatory = $true, Position = 1 )]
		[string] $ComputerName,

		[Parameter( Mandatory = $true, Position = 2 )]
		[System.Management.Automation.Credential()]
		[PSObject] $Credential,

		[Parameter( Position = 3 )]
		[hashtable] $Arguments,

		[Parameter( Position = 4 )]
		[string] $RemoteScriptDir = "C:\temp"
	)

	try
	{
		$argsStr = ""
		if( $Arguments -ne $null )
		{
			$Arguments.Keys | Foreach { $argsStr += "-$($_) `"$($Arguments[$_])`" " }
			$argsStr = $argsStr.Trim()
		}

		Write-Verbose "Invoking script $ScriptPath on $ComputerName with arguments '$argsStr'."

		if( Test-LocalComputer $ComputerName )
		{
			Test-LocalPowershellContext
			& $ScriptPath @Arguments
			return
		}

		# In order to be able to pass our arguments to the script using named parameters we can't use the
		# -FilePath overload for Invoke-Command, because it requires an -ArgumentList with positonal parameters.
		# So instead we "manually" perform the work that Invoke-Command -FilePath does by sending the script's
		# content to the remote machine using WinRM.
		$scriptContent = Get-Content -Raw $ScriptPath
		Invoke-Command -ComputerName $ComputerName -HideComputerName -ScriptBlock {

			$scriptDir = $using:RemoteScriptDir
			$scriptName = Split-Path $using:ScriptPath -Leaf
			$scriptPath = Join-Path $scriptDir $scriptName

			if( -not ( Test-Path $scriptDir ) )
			{
				New-Item -ItemType Directory -Path $scriptDir -Force | Out-Null
			}
			if( Test-Path $scriptPath )
			{
				Write-Warning "Remote script with path $scriptPath already exists and will be overwritten."
			}

			$using:scriptContent | Out-File -FilePath $scriptPath -Force

			try
			{
				$passedArgs = $using:Arguments
				& $scriptPath @passedArgs
			}
			catch
			{
				# Write out as much info as we can get here, since we ran the script on the remote
				# machine as a scriptblock we don't get much good info about line numbers or where
				# the exception really occurred, so anything we can do might help debugging.
				$info = $error[0].CategoryInfo
				$errId = $error[0].FullyQualifiedErrorId
				$scriptTrace = $error[0].ScriptStackTrace
				Write-Host "An error occured while executing $($ScriptPath):`n" -ForegroundColor Red
				Write-Host "CategoryInfo: $info" -ForegroundColor Red
				Write-Host "FullyQualifiedErrorId: $errId`n" -ForegroundColor Red
				Write-Host "$scriptTrace`n" -ForegroundColor Red
				throw $_
			}

		} -Authentication Credssp -Credential $Credential -ConfigurationName "Microsoft.Powershell32" -ErrorAction Stop
	}
	catch
	{
		## Debugging code for CredSSP problems ####################
		if( $_.Exception.Message -match "WinRM" )
		{
			# Figure out our DC and our site name
			switch -regex ( nltest /dsgetdc:testdomain /kdc )
			{
				"DC: \\\\(.+)" { $dc = $matches[1] }
				"Dc Site Name: (.+)" { $site = $matches[1] }
			}

			Write-Host "My KDC = $dc"

			# Find other DCs in our site
			$dcs = switch -regex ( nltest /dclist:testdomain )
			{
				"\s+(\w+(\.\w+)+).*Site: $site" { $matches[1] }
			}

			# Figure out which DCs know about the test computer
			Write-Host "DCs which know about $ComputerName"
			$dcs | % {
				if( Get-ADObject -Filter { Name -eq $ComputerName } -Server $_ -ErrorAction Continue )
				{
					Write-Host $_
				}
			}
		}

		throw "Failed to execute $ScriptPath on $ComputerName with arguments $($argsStr): $_."
	}
}
