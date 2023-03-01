/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */     

package AdapterPackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import DTNRouting.*;
import java.util.Random;
/**
 *
 * @author nil
 */
public class MyActionAdapter implements ActionListener {
	dtnrouting dtn;
	UpdateInformation updateInfo=new UpdateInformation();;
	Random rand;

	public MyActionAdapter(dtnrouting dtn)
	{
		this.dtn=dtn;
	}


	public void actionPerformed (ActionEvent ae)

	{
		String buttonname;

		buttonname=ae.getActionCommand();

	


		// CREATE NODE   
		if (buttonname.equals("Node"))
		{

			CreateNode cnodeObj=new CreateNode();
			cnodeObj.GenerateFrame();
		}


		// CLEAR SIMULATION
		else if (buttonname.equals("Clear"))
		{
			
			updateInfo.clearSettings();
		}

		// RUN SIMULATION
		else if(buttonname.equals("Run"))
		{
			// SET THE SIMULATION DELAY
			dtnrouting.timer=0;
			updateInfo.simulationSettings();
			dtnrouting.SIMULATION_RUNNING=true;
		}


	}
}
