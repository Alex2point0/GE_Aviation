package com.ge.ems.cfoqa.tests.eventInteraction;

import com.ge.ems.cfoqa.pages.CfoqaEventContextView;
import com.ge.ems.cfoqa.pages.CfoqaEventInteractionView;
import com.ge.ems.cfoqa.pages.CfoqaNavigation;
import com.ge.ems.cfoqa.pages.CfoqaViewEvent;
import com.ge.ems.cfoqa.tests.CfoqaTest;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ConfirmViewerElements extends CfoqaTest {

    private CfoqaEventContextView contextView;
    private CfoqaEventInteractionView interactionView;

    @BeforeMethod
    public void beforeMethod(){
        if(firstTest) {
            CfoqaNavigation nav = new CfoqaNavigation(driver);
            CfoqaViewEvent viewEvent = nav.getViewEventPage();
            viewEvent.waitForPageLoad();
            contextView = viewEvent.viewEvent(0);
            interactionView = contextView.clickInteractButton();

            firstTest = false;
        }
    }

    @Test
    public void confirmPfdHorizon(){
        Assert.assertTrue(interactionView.pfdHorizonVisible(), "Artificial Horizon element in the PFD is not visible");
    }

    @Test
    public void confirmPfdAltitude(){
        Assert.assertTrue(interactionView.pfdAltitudeVisible(), "Altitude element in the PFD is not visible.");
    }

    @Test
    public void confirmPfdSpeed(){
        Assert.assertTrue(interactionView.pfdSpeedVisible(), "Speed element in the PFD is not visible.");
    }

    @Test
    public void confirmPfdFace(){
        Assert.assertTrue(interactionView.pfdFaceVisible(), "Face element in the PFD is not visible.");
    }

    @Test
    public void confirmPfdCdi(){
        Assert.assertTrue(interactionView.pfdCdiVisible(), "CDI element in the PFD is not visible.");
    }

    @Test
    public void confirmPfdDg(){
        Assert.assertTrue(interactionView.pfdDgVisible(), "DG element in the PFD is not visible.");
    }

    @Test
    public void confirmPfdPanel(){
        Assert.assertTrue(interactionView.pfdPanelVisible(), "Panel element in the PFD is not visible.");
    }

    @Test
    public void confirmTrajectoryCanvas(){
        Assert.assertTrue(interactionView.trajectoryCanvasVisible(), "Trajectory viewer canvas is not visible.");
    }

    @Test
    public void confirmApproachAltitudeLine(){
        Assert.assertTrue(interactionView.approachAltitudeVisible(), "Altitude line in the approach viewer is not visible.");
    }

    @Test
    public void confirmApproachTerrainLine(){
        Assert.assertTrue(interactionView.approachTerrainVisible(), "Terrain line in the approach viewer is not visible.");
    }
}
