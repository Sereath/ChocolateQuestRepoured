package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms;

import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.segments.RoomWalls;
import com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms.segments.WallOptions;
import com.teamcqr.chocolatequestrepoured.util.BlockPlacement;
import net.minecraft.util.EnumFacing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class RoomGridCell
{
    private enum CellState
    {
        UNUSED (0),       //empty and cannot build anything on this space
        BUILDABLE (1),    //empty but able to build on this space
        SELECTED (2),     //selected for building but not filled with a room
        POPULATED (3);    //filled with a room

        private final int value;

        CellState(int value)
        {
            this.value = value;
        }

        private boolean isAtLeast(CellState state)
        {
            return value >= state.value;
        }

        private boolean isLessThan(CellState state)
        {
            return value < state.value;
        }
    }

    private RoomGridPosition gridPosition;
    private CellState state;
    private boolean reachable;
    private boolean partOfMainStruct;
    private CastleRoom room;
    private boolean narrow;
    private RoomWalls walls;

    public RoomGridCell(int floor, int x, int z, CastleRoom room)
    {
        this.gridPosition = new RoomGridPosition(floor, x, z);
        this.state = CellState.UNUSED;
        this.reachable = false;
        this.partOfMainStruct = false;
        this.room = room;
        this.walls = new RoomWalls();
    }

    public RoomGridCell(int floor, int x, int z)
    {
        this.gridPosition = new RoomGridPosition(floor, x, z);
        this.state = CellState.UNUSED;
        this.reachable = false;
        this.partOfMainStruct = false;
        this.room = null;
        this.walls = new RoomWalls();
    }

    public RoomGridCell(RoomGridPosition gridPosition, CastleRoom room)
    {
        this.gridPosition = gridPosition;
        this.state = CellState.UNUSED;
        this.reachable = false;
        this.partOfMainStruct = false;
        this.room = room;
        this.walls = new RoomWalls();
    }

    public RoomGridCell(RoomGridPosition gridPosition)
    {
        this.gridPosition = gridPosition;
        this.state = CellState.UNUSED;
        this.reachable = false;
        this.partOfMainStruct = false;
        this.room = null;
        this.walls = new RoomWalls();
    }

    public void setReachable()
    {
        this.reachable = true;
    }

    public boolean isReachable()
    {
        return reachable;
    }

    public void setAsMainStruct()
    {
        partOfMainStruct = true;
    }

    public boolean isMainStruct()
    {
        return partOfMainStruct;
    }

    public void setNarrow()
    {
        narrow = true;
    }

    public boolean isNarrow()
    {
        return narrow;
    }

    public void setBuildable()
    {
        if (state.isLessThan(CellState.BUILDABLE))
        {
            state = CellState.BUILDABLE;
        }
    }

    public boolean isBuildable()
    {
        return (state.isAtLeast(CellState.BUILDABLE));
    }

    public void selectForBuilding()
    {
        if (state.isLessThan(CellState.SELECTED))
        {
            state = CellState.SELECTED;
        }
    }

    public boolean isSelectedForBuilding()
    {
        return (state.isAtLeast(CellState.SELECTED));
    }

    public boolean isPopulated()
    {
        return (state.isAtLeast(CellState.POPULATED));
    }

    public boolean needsRoomType()
    {
        return (state == CellState.SELECTED);
    }

    public boolean isValidPathStart()
    {
        return !isReachable() && isPopulated() && !this.room.isTower();
    }

    public boolean isValidPathDestination()
    {
        return isReachable() && isPopulated() && !this.room.isTower();
    }

    public double distanceTo(RoomGridCell destCell)
    {
        int distX = Math.abs(getGridX() - destCell.getGridX());
        int distZ = Math.abs(getGridZ() - destCell.getGridZ());
        return (Math.hypot(distX, distZ));
    }

    public CastleRoom getRoom()
    {
        return room;
    }

    public void setRoom(CastleRoom room)
    {
        this.room = room;
        this.state = CellState.POPULATED;
    }

    public boolean reachableFromSide(EnumFacing side)
    {
        if (room != null)
        {
            return room.reachableFromSide(side);
        }
        else
        {
            return false;
        }
    }

    public boolean hasWallOnSide(EnumFacing side) { return walls.hasWallOnSide(side); }

    public boolean hasDoorOnSide(EnumFacing side)
    {
        return walls.hasDoorOnside(side);
    }

    public void addDoorOnSideCentered(EnumFacing side)
    {
        walls.addCenteredDoor(side);
    }
    public void addDoorOnSideRandom(EnumFacing side) { walls.addRandomDoor(side); }

    public void addOuterWall(EnumFacing side)
    {
        walls.addOuter(side);
    }

    public void addInnerWall(EnumFacing side)
    {
        walls.addInner(side);
    }

    public void saveRoomWalls()
    {
        if (this.room != null)
        {
            room.setWalls(walls);
        }
    }

    public void generateIfPopulated(ArrayList<BlockPlacement> blocks)
    {
        if (state == CellState.POPULATED)
        {
            room.generate(blocks);
        }
    }

    public RoomGridPosition getGridPosition()
    {
        return gridPosition;
    }

    public int getFloor()
    {
        return this.gridPosition.getFloor();
    }

    public int getGridX()
    {
        return this.gridPosition.getX();
    }

    public int getGridZ()
    {
        return this.gridPosition.getZ();
    }
}
