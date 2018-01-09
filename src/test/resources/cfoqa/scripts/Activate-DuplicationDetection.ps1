[CmdletBinding()]
param
(
    [Parameter( Mandatory = $true )]
    [string] $Username,

    [Parameter( Mandatory = $true)]
    [string] $Password,

    [Parameter( Mandatory = $true)]
    [string] $ComputerName,
	
	[Parameter( Mandatory = $true)]
	[string] $active,

    [Parameter()]
    [string] $RemoteScriptDir = "C:\temp"
)

$ErrorActionPreference = "Stop"

. "$PSScriptRoot\Invoke-ScriptOnComputer.ps1"

$PWord = ConvertTo-SecureString -String $password -AsPlainText -Force

$Credential = New-Object -TypeName "System.Management.Automation.PSCredential" -ArgumentList $Username, $PWord

$invokeArgs = @{
    ComputerName = $ComputerName
    Credential = $Credential
    RemoteScriptDir = $RemoteScriptDir
}

Invoke-ScriptOnComputer "$PSScriptRoot\Update-DuplicationDetection.ps1" @invokeArgs -Arguments @{
    Active = $active
}
