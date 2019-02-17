package org.team3128.gromit.main;

import org.team3128.common.NarwhalRobot;
import org.team3128.common.drive.SRXInvertCallback;
import org.team3128.common.drive.SRXTankDrive;
import org.team3128.common.drive.callibrationutility.DriveCallibrationUtility;
import org.team3128.common.hardware.misc.Piston;
import org.team3128.common.hardware.misc.TwoSpeedGearshift;
import org.team3128.common.listener.ListenerManager;
import org.team3128.common.listener.POVValue;
import org.team3128.common.listener.controllers.ControllerExtreme3D;
import org.team3128.common.listener.controltypes.Button;
import org.team3128.common.listener.controltypes.POV;
import org.team3128.common.narwhaldashboard.NarwhalDashboard;
import org.team3128.common.util.Constants;
import org.team3128.common.util.Log;
import org.team3128.common.util.Wheelbase;
import org.team3128.common.util.units.Angle;
import org.team3128.common.util.units.Length;
import org.team3128.gromit.mechanisms.Climber;
import org.team3128.gromit.mechanisms.FourBar;
// import org.team3128.gromit.mechanisms.GroundIntake;
import org.team3128.gromit.mechanisms.Lift;
import org.team3128.gromit.mechanisms.LiftIntake;
import org.team3128.gromit.mechanisms.OptimusPrime;
import org.team3128.gromit.mechanisms.FourBar.FourBarState;
import org.team3128.gromit.mechanisms.Lift.LiftControlMode;
// import org.team3128.gromit.mechanisms.GroundIntake.GroundIntakeState;
import org.team3128.gromit.mechanisms.Lift.LiftHeightState;
import org.team3128.gromit.mechanisms.LiftIntake.LiftIntakeState;
import org.team3128.gromit.mechanisms.OptimusPrime.RobotState;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class MainGromit extends NarwhalRobot{

    public AHRS ahrs;
	public ADXRS450_Gyro gyro;
	
	// Drivetrain
	public SRXTankDrive drive;

	public TwoSpeedGearshift gearshift;
	public Piston gearshiftPiston;
	public double shiftUpSpeed, shiftDownSpeed;

    public double wheelCirc;
    public double gearRatio;
    public double wheelbase;
	public int driveMaxSpeed;
	
	public SRXInvertCallback teleopInvertCallback, autoInvertCallback;
	public double leftSpeedScalar, rightSpeedScalar;

	public DriveCallibrationUtility dcu;
    
    // Drive Motors 
	public TalonSRX leftDriveLeader;
	public VictorSPX leftDriveFollower;
	public TalonSRX rightDriveLeader;
	public VictorSPX rightDriveFollower;

	// Pneumatics
	public Compressor compressor;

	// Four-Bar
	public FourBar fourBar;
	public FourBarState fourBarState;
	public TalonSRX fourBarMotor;
	public DigitalInput fourBarLimitSwitch;
	public double fourBarSwitchPosition;
	public int fourBarMaxVelocity;

	// Ground Intake
	// public GroundIntake groundIntake;
	// public GroundIntakeState groundIntakeState;
	public VictorSPX groundIntakeMotor;
	public Piston groundIntakePistons;

	// Lift
	public Lift lift;
	public LiftHeightState liftState;
	public TalonSRX liftMotorLeader;
	public VictorSPX liftMotorFollower;
	public DigitalInput liftLimitSwitch;
	public int liftSwitchPosition, liftMaxVelocity;

	// Lift Intake
	public LiftIntake liftIntake;
	public LiftIntakeState liftIntakeState;
	public VictorSPX liftIntakeMotor;
	//public VictorSPX liftIntakeMotorFollower;
	public Piston demogorgonPiston;
	public DigitalInput cargoBumperSwitch;

	// Optimus Prime!
	public OptimusPrime optimusPrime;

	// Climb
	public Piston climbPiston;
	public TalonSRX climbMotor;

	public Climber climber;

	// Controls
	public Joystick leftJoystick;
	public Joystick rightJoystick;
	public ListenerManager listenerLeft;
	public ListenerManager listenerRight;

	// Miscellaneous
	public PowerDistributionPanel powerDistPanel;

	public DriverStation ds;
	public boolean override = false;

	public CommandGroup enterIntake, exitIntake;

	// 4200
	public double maxLiftSpeed = 0;

	// 7510
	public double minLiftSpeed = 0;

	public enum ManualControlMode {
        LIFT,
        FOUR_BAR;
    }
	ManualControlMode manualControMode = ManualControlMode.LIFT;
	
	public enum GameElement {
		CARGO("cargo"),
		HATCH_PANEL("hatch_panel");

		private String name;
		private GameElement(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
	GameElement currentGameElement = GameElement.CARGO;

	public enum ScoreLevel {
		TOP("top"),
		MID("mid"),
		LOW("low");

		private String name;
		private ScoreLevel(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
 	ScoreLevel targetScoreLevel = ScoreLevel.LOW;

	public enum ScoreStructure {
		CARGO_SHIP,
		ROCKET;
	}
	ScoreStructure targetStructure = ScoreStructure.CARGO_SHIP;

    @Override
    protected void constructHardware() {
		// Construct and Configure Drivetrain
		leftDriveLeader = new TalonSRX(10);
		leftDriveFollower = new VictorSPX(11);
		rightDriveLeader = new TalonSRX(15);
		rightDriveFollower = new VictorSPX(16);

		leftDriveLeader.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Constants.CAN_TIMEOUT);
		leftDriveFollower.follow(leftDriveLeader);

        rightDriveLeader.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, Constants.CAN_TIMEOUT);
		rightDriveFollower.follow(rightDriveLeader);
		
		SRXTankDrive.initialize(rightDriveLeader, leftDriveLeader, wheelCirc, 1, wheelbase, driveMaxSpeed, teleopInvertCallback, autoInvertCallback);
        drive = SRXTankDrive.getInstance();

		drive.setLeftSpeedScalar(leftSpeedScalar);
		drive.setRightSpeedScalar(rightSpeedScalar);
		
		gearshift = new TwoSpeedGearshift(false, gearshiftPiston);
		drive.addShifter(gearshift, shiftUpSpeed, shiftDownSpeed);

		// Instantiate gryoscopes
		ahrs = new AHRS(SPI.Port.kMXP); 
		ahrs.reset();
		
		gyro = new ADXRS450_Gyro();
		gyro.calibrate();

		// DCU
		DriveCallibrationUtility.initialize(ahrs);
		dcu = DriveCallibrationUtility.getInstance();

		compressor = new Compressor();


		// Create Four-Bar
		fourBarMotor = new TalonSRX(30);

		FourBar.initialize(fourBarMotor, fourBarState, fourBarLimitSwitch, fourBarSwitchPosition, fourBarMaxVelocity);
		fourBar = FourBar.getInstance();



		// Create Ground Intake
		// groundIntakeState = GroundIntake.GroundIntakeState.RETRACTED;
		// groundIntakeMotor = new VictorSPX(99);

		// GroundIntake.initialize(groundIntakeMotor, groundIntakeState, groundIntakePistons, false);
		// groundIntake = GroundIntake.getInstance();


		// Create Lift
		liftState = LiftHeightState.BASE;
		liftMotorLeader = new TalonSRX(20);
		liftMotorFollower = new VictorSPX(21);

		liftMotorFollower.follow(liftMotorLeader);

		Lift.initialize(liftState, liftMotorLeader, liftLimitSwitch, liftMaxVelocity);
		lift = Lift.getInstance();


		// Create Lift Intake
		liftIntakeState = LiftIntake.LiftIntakeState.STOPPED;
		liftIntakeMotor = new VictorSPX(31);
		//liftIntakeMotorFollower = new VictorSPX(32);
		//liftIntakeMotorFollower.set(ControlMode.Follower, liftIntakeMotorLeader.getDeviceID());
		liftIntakeMotor.setInverted(true);

		LiftIntake.initialize(liftIntakeMotor, liftIntakeState, demogorgonPiston, cargoBumperSwitch);
		liftIntake = LiftIntake.getInstance();
		demogorgonPiston.setPistonOff();

		// Create Optimus Prime
		OptimusPrime.initialize();
		optimusPrime = OptimusPrime.getInstance();

		// Create the Climber
		climbMotor = new TalonSRX(40);
		
		Climber.initialize(climbPiston, climbMotor);
		climber = Climber.getInstance();

		// Instantiate PDP
		powerDistPanel = new PowerDistributionPanel();

		ds = DriverStation.getInstance();

		// Setup listeners
        leftJoystick = new Joystick(1);
		listenerLeft = new ListenerManager(leftJoystick);
		addListenerManager(listenerLeft);

		rightJoystick = new Joystick(0);
		listenerRight = new ListenerManager(rightJoystick);
		addListenerManager(listenerRight);

		NarwhalDashboard.addButton("fourbar_high", (boolean down) -> {
			if (down) {
				fourBar.setState(FourBarState.HIGH);
			}
		});

		NarwhalDashboard.addButton("fourbar_ship_low", (boolean down) -> {
			if (down) {
				fourBar.setState(FourBarState.SHIP_LOADING);
			}
		});

		NarwhalDashboard.addButton("fourbar_rocket_low", (boolean down) -> {
			if (down) {
				fourBar.setState(FourBarState.ROCKET_LOW);
			}
		});

		NarwhalDashboard.addButton("fourbar_intake", (boolean down) -> {
			if (down) {
				fourBar.setState(FourBarState.CARGO_INTAKE);
			}
		});


		NarwhalDashboard.addButton("lift_base", (boolean down) -> {
			if (down) {
				lift.setState(LiftHeightState.BASE);
			}
		});

		NarwhalDashboard.addButton("lift_low", (boolean down) -> {
			if (down) {
				lift.setState(RobotState.getOptimusState(currentGameElement, ScoreLevel.LOW).targetLiftState);
			}
		});

		NarwhalDashboard.addButton("lift_mid", (boolean down) -> {
			if (down) {
				lift.setState(RobotState.getOptimusState(currentGameElement, ScoreLevel.MID).targetLiftState);
			}
		});

		NarwhalDashboard.addButton("lift_top", (boolean down) -> {
			if (down) {
				lift.setState(RobotState.getOptimusState(currentGameElement, ScoreLevel.TOP).targetLiftState);
			}
		});

		
		NarwhalDashboard.addButton("climb_12", (boolean down) -> {
			if (down) {
				climber.new CmdClimb1to2().start();
			}
		});

		NarwhalDashboard.addButton("climb_23", (boolean down) -> {
			if (down) {
				climber.new CmdClimb2to3().start();
			}
		});		

		dcu.initNarwhalDashboard();
    }

    @Override
    protected void setupListeners() {
		// REGULAR CONTROLS

		// Drive
        listenerRight.nameControl(ControllerExtreme3D.JOYY, "MoveForwards");
		listenerRight.nameControl(ControllerExtreme3D.TWIST, "MoveTurn");
		listenerRight.nameControl(ControllerExtreme3D.THROTTLE, "Throttle");
		listenerRight.addMultiListener(() ->
		{
			double x = listenerRight.getAxis("MoveForwards");
			double y = listenerRight.getAxis("MoveTurn");
			double t = listenerRight.getAxis("Throttle") * -1;

			drive.arcadeDrive(x, -0.8 * y, t, true);
		}, "MoveForwards", "MoveTurn", "Throttle");

		listenerRight.nameControl(new Button(2), "Gearshift");
		listenerRight.addButtonDownListener("Gearshift", drive::shift);

		// Optimus Prime Controls
		listenerRight.nameControl(new POV(0), "IntakePOV");
		listenerRight.addListener("IntakePOV", (POVValue pov) ->
		{
			switch (pov.getDirectionValue()) {
				case 1:
				case 7:
				case 8:
					liftIntake.setState(LiftIntakeState.CARGO_OUTTAKE);
				case 3:
				case 4:
				case 5:
					if (exitIntake != null) exitIntake.cancel();

					enterIntake = (optimusPrime.new CmdEnterIntakeMode());
					enterIntake.start();
					break;
				case 0:
					if (enterIntake != null) enterIntake.cancel();

					exitIntake = (optimusPrime.new CmdExitIntakeMode());
					exitIntake.start();
					break;

				default:
					break;
			}
		});

		listenerRight.nameControl(ControllerExtreme3D.TRIGGER, "Score");
		listenerRight.addButtonDownListener("Score", () -> {
			optimusPrime.setState(RobotState.getOptimusState(currentGameElement, targetScoreLevel));
		});
		listenerRight.addButtonUpListener("Score", () -> {
			optimusPrime.setState(RobotState.REST);
		});

		// Game Element Controls
		listenerRight.nameControl(new Button(4), "SelectHatchPanel");
		listenerRight.addButtonDownListener("SelectHatchPanel", () -> {
			currentGameElement = GameElement.HATCH_PANEL;
		});

		listenerRight.nameControl(new Button(3), "SelectCargo");
		listenerRight.addButtonDownListener("SelectCargo", () -> {
			currentGameElement = GameElement.CARGO;
		});

		// Scoring Structure Controls
		listenerRight.nameControl(new Button(6), "SelectCargoShip");
		listenerRight.addButtonDownListener("SelectCargoShip", () -> {
			targetStructure = ScoreStructure.CARGO_SHIP;
			targetScoreLevel = ScoreLevel.LOW;
		});

		// Height Controls
		listenerRight.nameControl(new Button(7), "SelectTopLevel");
		listenerRight.addButtonDownListener("SelectTopLevel", () -> {
			targetStructure = ScoreStructure.ROCKET;
			targetScoreLevel = ScoreLevel.TOP;
		});

		listenerRight.nameControl(new Button(9), "SelectMidLevel");
		listenerRight.addButtonDownListener("SelectMidLevel", () -> {
			targetStructure = ScoreStructure.ROCKET;
			targetScoreLevel = ScoreLevel.MID;
		});

		listenerRight.nameControl(new Button(11), "SelectLowLevel");
		listenerRight.addButtonDownListener("SelectLowLevel", () -> {
			targetStructure = ScoreStructure.ROCKET;
			targetScoreLevel = ScoreLevel.LOW;
		});

		// Compressor
		listenerRight.nameControl(new Button(10), "StartCompressor");
		listenerRight.addButtonDownListener("StartCompressor", () ->
		{
			compressor.start();
			Log.info("MainGuido", "Starting Compressor");
		});

		listenerRight.nameControl(new Button(12), "StopCompressor");
		listenerRight.addButtonDownListener("StopCompressor", () ->
		{
			compressor.stop();
			Log.info("MainGuido", "Stopping Compressor");
		});

		// MANUAL CONTROLS AND OVERRIDES

		listenerLeft.nameControl(ControllerExtreme3D.TRIGGER, "Override");
		listenerLeft.nameControl(new Button(2), "ManualMode");
		listenerLeft.nameControl(ControllerExtreme3D.JOYY, "ManualControl");

		listenerLeft.addMultiListener(() -> {
			if (listenerLeft.getButton("ManualMode")) {
				lift.override = false;
				lift.powerControl(0);

				fourBar.override = listenerLeft.getButton("Override");
				fourBar.powerControl(listenerLeft.getAxis("ManualControl"));
			}
			else {
				fourBar.override = false;
				fourBar.powerControl(0);

				lift.override = listenerLeft.getButton("Override");
				lift.powerControl(listenerLeft.getAxis("ManualControl"));
			}
		}, "ManualMode", "Override", "ManualControl");

		
		listenerLeft.nameControl(new POV(0), "ManualIntakePOV");
        listenerLeft.addListener("ManualIntakePOV", (POVValue povVal) -> {
            switch (povVal.getDirectionValue()) {
                case 8:
                case 1:
                case 7:
					liftIntake.setState(LiftIntakeState.CARGO_OUTTAKE);
					break;
                case 3:
                case 4:
                case 5:
					liftIntake.setState(LiftIntakeState.CARGO_INTAKE);
					break;
				case 2:
				case 6:
					demogorgonPiston.setPistonOn();
					break;
                default:
					liftIntake.setState(LiftIntakeState.STOPPED);
					demogorgonPiston.setPistonOff();
					break;
            }
		});
		
		listenerLeft.nameControl(new Button(5), "DemogorgonGrab");
        listenerLeft.addButtonDownListener("DemogorgonGrab", () -> {
            demogorgonPiston.setPistonOff();
        });
        listenerLeft.addButtonUpListener("DemogorgonGrab", () -> {
            demogorgonPiston.setPistonOn();
        });

        listenerLeft.nameControl(new Button(9), "ClimbPistonExtend");
        listenerLeft.addButtonDownListener("ClimbPistonExtend", () -> {
            climbPiston.setPistonOn();
        });

        listenerLeft.nameControl(new Button(10), "ClimbPistonRetract");
        listenerLeft.addButtonDownListener("ClimbPistonRetract", () -> {
            climbPiston.setPistonOff();
        });

        listenerLeft.nameControl(new Button(11), "BackLegDown");
        listenerLeft.nameControl(new Button(12), "BackLegUp");
        listenerLeft.addMultiListener(() -> {
            if (listenerLeft.getButton("BackLegDown") && 
               !listenerLeft.getButton("BackLegUp")) {
                climbMotor.set(ControlMode.PercentOutput, +1.0);
            }
            else if (listenerLeft.getButton("BackLegUp") &&
                    !listenerLeft.getButton("BackLegDown")) {
                climbMotor.set(ControlMode.PercentOutput, -1.0);
            }
            else {
                climbMotor.set(ControlMode.PercentOutput, 0.0);
            }
        }, "BackLegDown", "BackLegUp");
		
		listenerLeft.nameControl(new Button(7), "SetLiftZero");
		listenerLeft.addButtonDownListener("SetLiftZero", () ->
		{
			liftMotorLeader.setSelectedSensorPosition(0, 0, Constants.CAN_TIMEOUT);
		});

		listenerLeft.nameControl(new Button(8), "SetFourBarBase");
		listenerLeft.addButtonDownListener("SetFourBarBase", () -> {
			fourBar.setCurrentAngle(-90 * Angle.DEGREES);
		});
	}
	
	@Override
	protected void constructAutoPrograms()
	{

	}

	@Override
	protected void teleopInit()
	{
		fourBar.brakeControl();
	}
	
	@Override
	protected void disabledInit()
	{

	}
	
	@Override
	protected void disabledPeriodic()
	{

	}

	@Override
	protected void autonomousInit()
	{
		
	}

	@Override
	protected void updateDashboard()
	{	
		SmartDashboard.putBoolean("Lift: Can Raise", lift.canRaise);
		SmartDashboard.putBoolean("Lift: Can Lower", lift.canLower);

		SmartDashboard.putNumber("Lift Height (inches)", lift.getCurrentHeight() / Length.in);


		SmartDashboard.putBoolean("Four Bar: Can Raise", fourBar.canRaise);
		SmartDashboard.putBoolean("Four Bar: Can Lower", fourBar.canLower);

		SmartDashboard.putNumber("Four Bar Angle (degrees)", fourBar.getCurrentAngle());

		SmartDashboard.putNumber("Back Leg Position (nu)", climbMotor.getSelectedSensorPosition());

		maxLiftSpeed = Math.max(maxLiftSpeed, liftMotorLeader.getSelectedSensorVelocity());
		SmartDashboard.putNumber("Max Upward Lift Speed", maxLiftSpeed);

		minLiftSpeed = Math.min(minLiftSpeed, liftMotorLeader.getSelectedSensorVelocity());
		SmartDashboard.putNumber("Min Lift Speed", minLiftSpeed);


		NarwhalDashboard.put("scoring_height", (targetStructure == ScoreStructure.CARGO_SHIP) ? "ship" : targetScoreLevel.getName());
		NarwhalDashboard.put("game_element", currentGameElement.getName());

		dcu.tickNarwhalDashboard();
	}
}