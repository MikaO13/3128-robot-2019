package org.team3128.gromit.cvcommands;

import org.team3128.common.drive.SRXTankDrive;
import org.team3128.common.hardware.limelight.Limelight;
import org.team3128.common.util.Log;
import org.team3128.gromit.main.MainGromit.GameElement;
import org.team3128.gromit.main.MainGromit.ScoreTarget;
import org.team3128.gromit.mechanisms.OptimusPrime;
import org.team3128.gromit.mechanisms.OptimusPrime.RobotState;
import org.team3128.gromit.util.DeepSpaceConstants;

import edu.wpi.first.wpilibj.command.Command;

public class CmdAutOptimusPrime extends Command {
    SRXTankDrive drive;
    OptimusPrime optimusPrime;
    
    GameElement gameElement;
    ScoreTarget scoreTarget;
    double currentTY, tyThreshold;
    Limelight limelight;
    
    public CmdAutOptimusPrime(GameElement gameElement, ScoreTarget scoreTarget) {
        
    }
    
    @Override
    protected void initialize() {
        currentTY = limelight.getValue("ty", 1);
        if(gameElement == GameElement.CARGO){
            tyThreshold = DeepSpaceConstants.UPPER_TY_OPTIMUS_THRESHOLD;
        }
        else{
            tyThreshold = DeepSpaceConstants.LOWER_TY_OPTIMUS_THRESHOLD;
        }
        
        drive = SRXTankDrive.getInstance();
        optimusPrime = OptimusPrime.getInstance();
    }
    
    @Override
    protected void execute() {
        
    }
    
    @Override
    protected boolean isFinished() {
        currentTY = limelight.getValue("ty", 1);
        if(currentTY > tyThreshold){
            optimusPrime.setState(RobotState.getOptimusState(gameElement, scoreTarget));
            return true;
        }
        return false;
    }
    
    @Override
    protected void end() {
        Log.info("CmdAutOptimusPrime", "Command Completed");
    }
    
    @Override
    protected void interrupted() {
        end();
    }
}