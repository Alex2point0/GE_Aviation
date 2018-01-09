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
	[string] $MinFlightHours,

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

Invoke-ScriptOnComputer "$PSScriptRoot\Update-DiceAndDeidentifyMinFlightHours.ps1" @invokeArgs -Arguments @{
    MinFlightHours = $MinFlightHours
}
