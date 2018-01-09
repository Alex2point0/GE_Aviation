[CmdletBinding()]
param
(
    [Parameter( Mandatory = $true )]
	[string] $MinFlightHours
)

function Find-LocalDirAdi
{
    $shares = Get-WmiObject Win32_Share -ComputerName $env:COMPUTERNAME
    $adiShare = $shares | Where Name -eq "adi" | Select -First 1

    # Look for a network share first off.
    $shareRoot = "\\$env:COMPUTERNAME"
    if( $adiShare -ne $null )
    {
        return "$shareRoot\$($adiShare.Path[0])$\adi"
    }

    # Try the D$ share.
    $share = "$shareRoot\D$\adi"
    if( Test-Path $share -ErrorAction SilentlyContinue ) { return $share }

    # Try the C$ share.
    $share = "$shareRoot\C$\adi"
    if( Test-Path $share -ErrorAction SilentlyContinue ) { return $share }

    throw "Could not find the dirAdi admin share for $env:COMPUTERNAME"
}

$ErrorActionPreference = "Stop"

$dirAdi = Find-LocalDirAdi

. "$dirAdi\support\powershell\Load-AdiTools.ps1"

$dupe = Get-DPActivity -name "Flight Dice and Deidentify"
$config = [xml]$dupe.GetConfig()
$config.Root_Element.CDiceAndDeidentifyActivityConfig.MinFlightHours = $MinFlightHours
$dupe.SetConfig( $config.Root_Element.OuterXml )
