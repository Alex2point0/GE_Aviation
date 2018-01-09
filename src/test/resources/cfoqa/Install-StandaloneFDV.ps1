#
# .SYNOPSIS
# This script install FDV+ standalone.
# 
#
# .NOTES
# The work this script performs includes:
#	- Copying the install exe for the right branch to the requested local diskImages path.
#	- Running the FDV+ standalone install
#
#
# This script must be run under 32 bit powershell. For Invoke-Command that means running it with
# "-ConfigurationName Microsoft.Powershell32".
#
# If this script is executed remotely credential delegation has to be enabled. This can be done
# with CredSSP, or with a proper delegation setup for Kerberos and the -EnableNetworkAccess switch
# for Invoke-Command.
#
# .OUTPUTS
# FDV+ standalone installed
#
[CmdletBinding()]
param
(
	[Parameter( Position = 0, Mandatory = $true, HelpMessage = "Enter local DirAdi e.g. C:\ADI" )]
	[string] $LocalDirAdi,

	[Parameter( HelpMessage = "Enter install branch e.g. smallchanges (this is required if CdImageLocation is not specified)" )]
	[string] $Branch,

	[Parameter( HelpMessage = "The local directory where the disk image should be copied" )]
	[string] $LocalDiskImages = "c:\diskImages",

	[Parameter( HelpMessage = "The EMS CD location (this should point to the 'installer' folder). If not specified, this is determined automatically from the -Branch parameter" )]
	[string] $CdImageLocation,

	# This is Boolean and not Switch because we want to pass explicit $true or $false values for the parameter
	[Parameter( HelpMessage = "Determines whether the system stores exact dates or not" )]
	[boolean] $ExactDateSystem,

	[Parameter( HelpMessage = "Requests that the script generate minimal output" )]
	[switch] $Quiet
)

$ErrorActionPreference = "Stop"

# simple function to write a log message with a timestamp, using Out-Host so the script can return
# objects if it wants to.
function log( $msg ) { Write-Host "$(get-date): $msg" }

$machineName = $env:computername
$uncRoot = "\\$machineName"
$localRoot = $localDirAdi.Substring( 0, 2 )
$dirAdi = "$uncRoot\adi"
$adminShare = "$uncRoot\$($localDirAdi[0])$\adi"


# default the install.exe location if we didn't get one explicitly
if (! $cdImageLocation )
{
    $cdImageLocation = "\\testdomain\shares\buildImages\$branch\FDV+"
}

# Copy the install install.exe to local disk images, overwriting anything in $localDiskImages\installer\FDV+
# with the contents of $cdImageLocation. The copy is done with robocopy because it's not affected
# by path length restrictions when using /MIR, and because it supports slow networks better by
# retying individual files on disconnect (which can happen when copying the CD from Houston to
# a branch office).
$installerDir = "$localDiskImages\FDV+"
if( !( Test-Path $installerDir ) )
{
	New-Item -ItemType Directory -Path $installerDir -Force | Out-Null
}

# 5 retries with a 10 second delay.
log "Copying over install files..."
& robocopy.exe $cdImageLocation $installerDir /MIR /R:5 /W:10 /Z | Out-Null
if( $LASTEXITCODE -gt 6 )
{
	throw "Robocopy failed to copy the FDV+ installer from $cdImageLocation to $installerDir with the exit code $LASTEXITCODE."
}

log "Installing FDV+..."
$installer = Start-Process -Verb Runas $localDiskImages\FDV+\install.exe `
    -ArgumentList @( "/S /InstallUpdateSilent", "/DirAdi", "$adminShare", "/LogDirectory", "$adminShare\setupLog\" ) -PassThru -Wait
    
if( $installer.ExitCode -ne 0 )
{
    throw "Unable to install FDV+: installer returned $($installer.ExitCode)"
}
