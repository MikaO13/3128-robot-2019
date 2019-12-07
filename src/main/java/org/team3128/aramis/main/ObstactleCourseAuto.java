package org.team3128.aramis.main;

import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;
import org.team3128.common.drive.SRXTankDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ObstactleCourseAuto extends CommandGroup {
    public ObstactleCourseAuto() {
        SRXTankDrive drive = SRXTankDrive.getInstance();
        addSequential(drive.new CmdDriveStraight(125 * Length.in, .5, 2000));
        addSequential(drive.new CmdInPlaceTurn(90, Direction.LEFT, .5, 1000));
        addSequential(drive.new CmdDriveStraight(135 * Length.in, .5, 2000));
        addSequential(drive.new CmdInPlaceTurn(90, Direction.RIGHT, .5, 1000));
        addSequential(drive.new CmdDriveStraight(136 * Length.in, .5, 2000));
        addSequential(drive.new CmdInPlaceTurn(90, Direction.LEFT, .5, 1000));
        addSequential(drive.new CmdDriveStraight(130 * Length.in, .5, 2000));
        addSequential(drive.new CmdArcTurn(52 * Length.in, 180, Direction.RIGHT, .5, 2000));
        addSequential(drive.new CmdInPlaceTurn(360, Direction.LEFT, .5, 2000));
    }
}