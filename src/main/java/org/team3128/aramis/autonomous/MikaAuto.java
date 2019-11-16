package org.team3128.aramis.autonomous;

import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;

import org.team3128.common.drive.SRXTankDrive;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class MikaAuto extends CommandGroup {
    public MikaAuto() {
        SRXTankDrive drive = SRXTankDrive.getInstance();
        for (int cnt = 1; cnt <= 4; cnt++) {
            addSequential(drive.new CmdDriveStraight(100 * Length.in, .5, 5000));
            addSequential(drive.new CmdInPlaceTurn(90, Direction.LEFT, .5, 5000));
        }
        
        for (int cnt = 1; cnt <= 3; cnt++) {
            addSequential(drive.new CmdDriveStraight(100 * Length.in, .5, 5000));
            addSequential(drive.new CmdInPlaceTurn(120, Direction.LEFT, .5, 5000));
        }
    }
}