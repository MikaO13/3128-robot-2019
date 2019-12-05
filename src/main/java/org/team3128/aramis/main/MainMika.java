package org.team3128.aramis.main;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import org.team3128.athos.autonomous.*;
import org.team3128.athos.util.PrebotDeepSpaceConstants;

import org.team3128.common.NarwhalRobot;
import org.team3128.common.drive.DriveCommandRunning;
import org.team3128.common.drive.SRXTankDrive;
import org.team3128.common.drive.SRXTankDrive.Wheelbase;
import org.team3128.common.drive.calibrationutility.DriveCalibrationUtility;
import org.team3128.common.hardware.limelight.Limelight;
import org.team3128.common.hardware.navigation.Gyro;
import org.team3128.common.hardware.navigation.NavX;
import org.team3128.common.util.Constants;
import org.team3128.common.util.units.Angle;
import org.team3128.common.util.units.Length;
import org.team3128.common.vision.CmdHorizontalOffsetFeedbackDrive;
import org.team3128.gromit.util.DeepSpaceConstants;
import org.team3128.common.util.Log;
import org.team3128.common.util.RobotMath;
import org.team3128.common.util.datatypes.PIDConstants;
import org.team3128.common.narwhaldashboard.NarwhalDashboard;
import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.controllers.ControllerExtreme3D;
import org.team3128.common.listener.controltypes.Button;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


//start typing the stuff to make this a robot that isn't non-functional and bad and blank and boring and stuff thanks lol
    // - Mason Holst, "Helpful Reminders", published November 2019

public class MainMika extends NarwhalRobot {
    public Joystick joystick;
    public ListenerManager lm;
    public SRXTankDrive tankDrive;

    public TalonSRX leftLeader;
    public VictorSPX leftFollower;
    public TalonSRX rightLeader;
    public VictorSPX rightFollower;

    public Command autoCommand;

	@Override
	protected void constructHardware()
	{
        joystick = new Joystick(0);
		lm = new ListenerManager(joystick);
        addListenerManager(lm);
        
        leftLeader = new TalonSRX(13);
        leftFollower = new VictorSPX(5);

        rightLeader = new TalonSRX(15);
        rightFollower = new VictorSPX(6);

        leftLeader.setInverted(false);
        leftFollower.setInverted(false);
        rightLeader.setInverted(true);
        rightFollower.setInverted(true);
        
        leftFollower.set(ControlMode.Follower, leftLeader.getDeviceID());
        rightFollower.set(ControlMode.Follower, rightLeader.getDeviceID());
        SRXTankDrive.initialize(leftLeader, rightLeader, 13.21*Length.in, 32.3*Length.in, 3700);

        tankDrive = SRXTankDrive.getInstance();
    }
    
    @Override
    protected void constructAutoPrograms() {
        
    }

	@Override
	protected void setupListeners() {
        lm.nameControl(ControllerExtreme3D.TWIST, "MoveTurn");
		lm.nameControl(ControllerExtreme3D.JOYY, "MoveForwards");
        lm.nameControl(ControllerExtreme3D.THROTTLE, "Throttle");	

        lm.addMultiListener(() -> {
            tankDrive.arcadeDrive(
                -0.7 * RobotMath.thresh(lm.getAxis("MoveTurn"), 0.1),
                -1.0 * RobotMath.thresh(lm.getAxis("MoveForwards"), 0.1),
                -1.0 * lm.getAxis("Throttle"),
                 true
            );
			
        }, "MoveTurn", "MoveForwards", "Throttle");

        lm.nameControl(new Button(2), "AutoButton");
        lm.addButtonUpListener("AutoButton", () -> {
            autoCommand = new MikaAuto();
            autoCommand.start();
        });
    }

    @Override
    protected void teleopPeriodic() {
    }

    @Override
    protected void updateDashboard() {

    }


    public static void main(String... args) {
        RobotBase.startRobot(MainMika::new);
    }
}