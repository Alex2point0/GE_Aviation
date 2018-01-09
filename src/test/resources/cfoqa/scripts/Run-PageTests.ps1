[CmdletBinding()]
Param(
    [string] $UploadFlight,

    [string] $ViewUpload,

    [string] $ViewEvent,

    [string] $ContextView,

    [string] $InteractionView,

    [string] $FullSuite
)

if($FullSuite -eq "true"){
    echo "Running full test suite."
    mvn test "-DsuiteFile=src/test/resources/cfoqa/cfoqa_suite.xml" "-DpropertiesFile=cfoqa/cfoqa.properties"
} else {
    $TestList = ""
    if($UploadFlight -eq "true"){
        $TestList += "Upload_Flight,"
    }

    if($ViewUpload -eq "true"){
        $TestList += "View_Upload,"
    }

    if($ViewEvent -eq "true"){
        $TestList += "View_Event,"
    }

    if($ContextView -eq "true"){
        $TestList += "Context_View,"
    }

    if($InteractionView -eq "true"){
        $TestList += "Interaction_View"
    }

    echo "Running $TestList"
    mvn test "-DsuiteFile=src/test/resources/cfoqa/cfoqa_suite.xml" "-DpropertiesFile=cfoqa/cfoqa.properties" "-P testNames" "-DtestNames=$TestList"
}

#if($error) { exit 1 }
