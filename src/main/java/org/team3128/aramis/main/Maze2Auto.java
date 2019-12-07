package org.team3128.aramis.main;

import org.team3128.common.util.enums.Direction;
import org.team3128.common.util.units.Length;
import org.team3128.common.drive.SRXTankDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class Maze2Auto extends CommandGroup {
    public Maze2Auto() {
        SRXTankDrive drive = SRXTankDrive.getInstance();
        addSequential(drive.new CmdDriveStraight(98 * Length.in, .5, 2000));
        addSequential(drive.new CmdInPlaceTurn(45, Direction.RIGHT, .5, 1000));
        addSequential(drive.new CmdDriveStraight(38 * Length.in, .5, 2000));
        addSequential(drive.new CmdInPlaceTurn(105, Direction.LEFT, .5, 1000));
        addSequential(drive.new CmdArcTurn(47*Length.in, 90, Direction.RIGHT, 0.5, 3000));
        addSequential(drive.new CmdDriveStraight(37 * Length.in, .5, 2000));
        addSequential(drive.new CmdInPlaceTurn(45, Direction.RIGHT, .5, 1000));
        addSequential(drive.new CmdDriveStraight(27 * Length.in, .5, 2000));
        addSequential(drive.new CmdInPlaceTurn(90, Direction.RIGHT, .5, 1000));
        addSequential(drive.new CmdDriveStraight(21 * Length.in, .5, 2000));
        addSequential(drive.new CmdInPlaceTurn(90, Direction.LEFT, .5, 1000));
        addSequential(drive.new CmdDriveStraight(116 * Length.in, .5, 2000));
    }
}