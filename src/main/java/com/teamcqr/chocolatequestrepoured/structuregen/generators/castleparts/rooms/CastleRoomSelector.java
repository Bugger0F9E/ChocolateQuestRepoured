package com.teamcqr.chocolatequestrepoured.structuregen.generators.castleparts.rooms;

import com.teamcqr.chocolatequestrepoured.util.BlockPlacement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.w3c.dom.NodeList;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.*;
import java.lang.Double;

public class CastleRoomSelector
{
    private static final int MAX_LAYERS = 5;

    private BlockPos startPos;
    private int floorHeight;
    private int roomSize;
    private int floorsPerLayer;
    private int maxFloors;
    private int usedFloors;
    private int numSlotsX;
    private int numSlotsZ;
    private Random random;
    private RoomGrid grid;

    public CastleRoomSelector(BlockPos startPos, int roomSize, int floorHeight, int floorsPerLayer, int numSlotsX, int numSlotsZ, Random random)
    {
        this.startPos = startPos;
        this.floorHeight = floorHeight;
        this.roomSize = roomSize;
        this.floorsPerLayer = floorsPerLayer;
        this.maxFloors = floorsPerLayer * MAX_LAYERS;
        this.numSlotsX = numSlotsX;
        this.numSlotsZ = numSlotsZ;
        this.random = random;

        this.grid = new RoomGrid(maxFloors, numSlotsX, numSlotsZ, random);
    }

    public void generateRooms(ArrayList<BlockPlacement> blocks)
    {
        for (CastleRoom room : grid.getRooms())
        {
            room.generate(blocks);
        }
    }

    public void fillRooms()
    {
        addMainBuilding();

        boolean vertical = random.nextBoolean();
        boolean largeBuilding = (numSlotsX >= 2 && numSlotsZ >= 2);

        if (largeBuilding)
        {
            for (int floor = 0; floor < usedFloors; floor++)
            {
                if (vertical)
                {
                    buildVerticalFloorHallway(floor);
                }
                else
                {
                    buildHorizontalFloorHallway(floor);
                }
                vertical = !vertical;
            }
        }

        addStairCases();

        ArrayList<RoomGridCell> unTyped = grid.getAllCellsWhere(c -> c.isSelectedForBuilding() &&
                                                                     !c.isPopulated());
        for (RoomGridCell selection : unTyped)
        {
            selection.setRoom(new CastleRoomKitchen(getRoomStart(selection), roomSize,
                    floorHeight, getPositionFromIndex(selection.getGridX(), selection.getGridZ())));
        }

        //addEntrances();
        placeOuterDoors();
        connectRooms();
        DetermineRoofs();

        //System.out.println(grid.printGrid());
    }

    private void addMainBuilding()
    {
        setFirstLayerBuildable();

        for (int layer = 0; layer < MAX_LAYERS; layer++)
        {
            int minX = grid.getMinBuildableXOnFloor(layer * floorsPerLayer);
            int maxX = grid.getMaxBuildableXOnFloor(layer * floorsPerLayer);
            int maxLenX = maxX - minX + 1;
            int minZ = grid.getMinBuildableZOnFloor(layer * floorsPerLayer);
            int maxZ = grid.getMaxBuildableZOnFloor(layer * floorsPerLayer);
            int maxLenZ = maxZ - minZ + 1;

            if (Math.min(maxLenX, maxLenZ) < 2)
            {
                break;
            }

            int mainRoomsX = randomSubsectionLength(maxLenX);
            int mainRoomsZ = randomSubsectionLength(maxLenZ);

            int offsetX = random.nextBoolean() ? 0 : maxLenX - mainRoomsX;
            int offsetZ = random.nextBoolean() ? 0 : maxLenZ - mainRoomsZ;

            for (int floor = 0; floor < floorsPerLayer; floor++)
            {
                usedFloors++;
                for (int x = 0; x < mainRoomsX; x++)
                {
                    for (int z = 0; z < mainRoomsZ; z++)
                    {
                        int xIndex = minX + offsetX + x;
                        int zIndex = minZ + offsetZ + z;
                        int floorIndex = floor + (layer * floorsPerLayer);
                        grid.selectCellForBuilding(floorIndex, xIndex, zIndex);
                        grid.setRoomAsMainStruct(floorIndex, xIndex, zIndex);

                        int oneLayerUp = floorIndex + floorsPerLayer;
                        if (oneLayerUp < maxFloors)
                        {
                            grid.setCellBuilable(oneLayerUp, xIndex, zIndex);
                        }
                    }
                }
            }

            int openCellsWest = offsetX;
            int openCellsNorth = offsetZ;
            int openCellsEast = maxLenX - mainRoomsX - offsetX;
            int openCellsSouth = maxLenZ - mainRoomsZ - offsetZ;
            int sideRoomsX, sideRoomsZ, startX, startZ;

            if (openCellsWest > 0)
            {
                sideRoomsX = random.nextInt(openCellsWest + 1);
                sideRoomsZ = random.nextInt(mainRoomsZ + 1);
                if (Math.min(sideRoomsX, sideRoomsZ) >= 1)
                {
                    startX = minX + offsetX - sideRoomsX;
                    startZ = random.nextBoolean() ? minZ + offsetZ : minZ + offsetZ + mainRoomsZ - sideRoomsZ;
                    grid.selectBlockOfCellsForBuilding((layer * floorsPerLayer), floorsPerLayer, startX, sideRoomsX, startZ, sideRoomsZ);
                }
            }

            if (openCellsNorth > 0)
            {
                sideRoomsX = random.nextInt(mainRoomsX + 1);
                sideRoomsZ = random.nextInt(openCellsNorth + 1);
                if (Math.min(sideRoomsX, sideRoomsZ) >= 1)
                {
                    startX = random.nextBoolean() ? minX + offsetX : minX + offsetX + mainRoomsX - sideRoomsX;
                    startZ = minZ + offsetZ - sideRoomsZ;
                    grid.selectBlockOfCellsForBuilding((layer * floorsPerLayer), floorsPerLayer, startX, sideRoomsX, startZ, sideRoomsZ);
                }
            }

            if (openCellsEast > 0)
            {
                sideRoomsX = random.nextInt(openCellsEast + 1);
                sideRoomsZ = random.nextInt(mainRoomsZ + 1);
                if (Math.min(sideRoomsX, sideRoomsZ) >= 1)
                {
                    startX = minX + offsetX + mainRoomsX;
                    startZ = random.nextBoolean() ? minZ + offsetZ : minZ + offsetZ + mainRoomsZ - sideRoomsZ;
                    grid.selectBlockOfCellsForBuilding((layer * floorsPerLayer), floorsPerLayer, startX, sideRoomsX, startZ, sideRoomsZ);
                }
            }

            if (openCellsSouth > 0)
            {
                sideRoomsX = random.nextInt(mainRoomsX + 1);
                sideRoomsZ = random.nextInt(openCellsSouth + 1);
                if (Math.min(sideRoomsX, sideRoomsZ) >= 1)
                {
                    startX = random.nextBoolean() ? minX + offsetX : minX + offsetX + mainRoomsX - sideRoomsX;
                    startZ = minZ + offsetZ + mainRoomsZ;
                    grid.selectBlockOfCellsForBuilding((layer * floorsPerLayer), floorsPerLayer, startX, sideRoomsX, startZ, sideRoomsZ);

                }
            }
        }
    }

    private void setFirstLayerBuildable()
    {
        for (int floor = 0; floor < floorsPerLayer; floor++)
        {
            for (int x = 0; x < numSlotsX; x++)
            {
                for (int z = 0; z < numSlotsZ; z++)
                {
                    grid.setCellBuilable(floor, x, z);
                }
            }
        }
    }

    private int randomSubsectionLength(int mainLength)
    {
        int rounding = (mainLength % 2 == 0) ? 0 : 1;
        int halfLen = mainLength / 2;
        return halfLen + random.nextInt(halfLen + rounding);
    }

    private void buildVerticalFloorHallway(int floor)
    {
        ArrayList<RoomGridCell> mainRooms = grid.getSelectedMainStructCells(floor);
        RoomGridCell rootRoom = mainRooms.get(random.nextInt(mainRooms.size()));
        ArrayList<RoomGridCell> hallwayRooms = grid.getSelectedCellsInColumn(floor, rootRoom.getGridX());

        for (RoomGridCell selection : hallwayRooms)
        {
            selection.setRoom(new CastleRoomHallway(getRoomStart(selection), roomSize, floorHeight,
                    getPositionFromIndex(selection.getGridX(), selection.getGridZ()), CastleRoomHallway.Alignment.VERTICAL));
            selection.setReachable();
        }
    }

    private void buildHorizontalFloorHallway(int floor)
    {
        ArrayList<RoomGridCell> mainRooms = grid.getSelectedMainStructCells(floor);
        RoomGridCell rootRoom = mainRooms.get(random.nextInt(mainRooms.size()));
        ArrayList<RoomGridCell> hallwayRooms = grid.getSelectedCellsInRow(floor, rootRoom.getGridZ());

        for (RoomGridCell selection : hallwayRooms)
        {
            selection.setRoom(new CastleRoomHallway(getRoomStart(selection), roomSize, floorHeight,
                    getPositionFromIndex(selection.getGridX(), selection.getGridZ()), CastleRoomHallway.Alignment.HORIZONTAL));
            selection.setReachable();
        }
    }

    private void placeOuterDoors()
    {
        ArrayList<RoomGridCell> lowerHallway = grid.getAllCellsWhere(r -> r.getFloor() == 0 &&
                                                                    r.isPopulated() &&
                                                                    r.getRoom().getRoomType() == CastleRoom.RoomType.HALLWAY);
        ArrayList<RoomGridCell> edgeCells = new ArrayList<>();

        for (RoomGridCell cell : lowerHallway)
        {
            for (EnumFacing side : EnumFacing.HORIZONTALS)
            {
                if (!grid.adjacentCellIsPopulated(cell, side))
                {
                    edgeCells.add(cell);
                }
            }
        }

        //pick a random cell from the hallway cells that are at the edge and add a door to the outside
        if (!edgeCells.isEmpty())
        {
            RoomGridCell doorCell = edgeCells.get(random.nextInt(edgeCells.size()));
            for (EnumFacing side : EnumFacing.HORIZONTALS)
            {
                if (!grid.adjacentCellIsPopulated(doorCell, side))
                {
                    doorCell.getRoom().addDoorOnSide(side);
                    doorCell.setReachable();
                    break;
                }
            }

        }
    }

    private boolean roomIsStaircaseOrLanding(CastleRoom room)
    {
        boolean result = false;
        if (room != null)
        {
            CastleRoom.RoomType type = room.getRoomType();
            if (type == CastleRoom.RoomType.STAIRCASE || type == CastleRoom.RoomType.LANDING)
            {
                result = true;
            }
        }

        return result;
    }


    private void addStairCases()
    {
        for (int floor = 0; floor < usedFloors; floor++)
        {
            final int f = floor; //lambda requires a final
            ArrayList<RoomGridCell> candidateCells = grid.getAllCellsWhere(r -> r.getFloor() == f &&
                    r.isSelectedForBuilding() &&
                    !r.isPopulated());
            for (RoomGridCell cell : candidateCells)
            {
                if (grid.cellBordersHallway(cell) && grid.adjacentCellIsSelected(cell, EnumFacing.UP))
                {
                    RoomGridCell aboveCell = grid.getAdjacentCell(cell, EnumFacing.UP);
                    if (!aboveCell.isPopulated() && grid.cellBordersHallway(aboveCell))
                    {
                        EnumFacing hallDirection = grid.getAdjacentHallwayDirection(cell);
                        CastleRoomStaircase stairs = new CastleRoomStaircase(getRoomStart(cell), roomSize, floorHeight,
                                getPositionFromIndex(cell.getGridX(), cell.getGridZ()), hallDirection);
                        cell.setRoom(stairs);
                        cell.getRoom().addDoorOnSide(grid.getAdjacentHallwayDirection(cell));

                        aboveCell.setRoom(new CastleRoomLanding(getRoomStart(aboveCell), roomSize, floorHeight,
                                getPositionFromIndex(aboveCell.getGridX(), aboveCell.getGridZ()), stairs));
                        aboveCell.getRoom().addDoorOnSide(grid.getAdjacentHallwayDirection(aboveCell));
                        break;
                    }
                }
            }
        }
    }

    private boolean roomBordersHallway(int floor, int x, int z)
    {
        return getAdjacentHallwayDirection(floor, x, z) != EnumFacing.DOWN;
    }

    private EnumFacing getAdjacentHallwayDirection(int floor, int x, int z)
    {
        CastleRoom neighborRoom;
        if (x != 0)
        {
            neighborRoom = grid.getRoomAt(floor, x - 1, z);
            if (neighborRoom != null && neighborRoom.roomType == CastleRoom.RoomType.HALLWAY)
            {
                return EnumFacing.WEST;
            }
        }
        if (z != 0)
        {
            neighborRoom = grid.getRoomAt(floor, x, z - 1);
            if (x != 0 && neighborRoom != null && neighborRoom.roomType == CastleRoom.RoomType.HALLWAY)
            {
                return EnumFacing.NORTH;
            }
        }
        if (x < numSlotsX - 1)
        {
            neighborRoom = grid.getRoomAt(floor, x + 1, z);
            if (neighborRoom != null && neighborRoom.roomType == CastleRoom.RoomType.HALLWAY)
            {
                return EnumFacing.EAST;
            }
        }
        if (z < numSlotsZ - 1)
        {
            neighborRoom = grid.getRoomAt(floor, x, z + 1);
            if (x != 0 && neighborRoom != null && neighborRoom.roomType == CastleRoom.RoomType.HALLWAY)
            {
                return EnumFacing.SOUTH;
            }
        }
        return EnumFacing.DOWN;
    }

    private void connectRooms()
    {
        System.out.println("Connecting rooms");
        for (int floor = 0; floor < maxFloors; floor++)
        {
            final int f = floor;
            ArrayList<RoomGridCell> unreachable = grid.getAllCellsWhere(c -> c.getFloor() == f && !c.isReachable() && c.isPopulated());
            ArrayList<RoomGridCell> reachable = grid.getAllCellsWhere(c -> c.getFloor() == f && c.isReachable() && c.isPopulated());

            while (!unreachable.isEmpty() && !reachable.isEmpty())
            {
                RoomGridCell srcRoom = unreachable.get(0);

                //going for the nearest doesnt always make the most interesting layout, may want to add noise
                RoomGridCell destRoom = findNearestReachableRoom(srcRoom, reachable);

                LinkedList<PathNode> destToSrcPath = findPathBetweenRooms(srcRoom, destRoom);

                for (PathNode node : destToSrcPath)
                {
                    RoomGridCell cell = grid.getCellAtLocation(node.getCell().getGridPosition());
                    if (cell != null)
                    {
                        if (node.getParent() != null)
                        {
                            if (cell.getRoom().hasWallOnSide(node.getParentDirection()))
                            {
                                cell.getRoom().addDoorOnSide(node.getParentDirection());
                            } else
                            {
                                RoomGridCell parentCell = grid.getAdjacentCell(cell, node.getParentDirection());
                                if (parentCell != null)
                                {
                                    parentCell.getRoom().addDoorOnSide(node.getParentDirection().getOpposite());
                                }
                            }
                        }
                        cell.setReachable();
                        unreachable.remove(cell);
                        reachable.add(cell);
                    }
                }
            }
        }
    }

    private class PathNode
    {
        private PathNode parent;
        private EnumFacing parentDirection;
        private RoomGridCell cell;
        private double f;
        private double g;
        private double h;

        private PathNode(PathNode parent, EnumFacing parentDirection, RoomGridCell cell, double g, double h)
        {
            this.parent = parent;
            this.parentDirection = parentDirection;
            this.cell = cell;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        public RoomGridCell getCell()
        {
            return cell;
        }

        public PathNode getParent()
        {
            return parent;
        }

        public double getF()
        {
            return f;
        }

        public double getG()
        {
            return g;
        }

        public EnumFacing getParentDirection()
        {
            return parentDirection;
        }

        public void updateParent(PathNode parent)
        {
            this.parent = parent;
        }

        public void updateG(double g)
        {
            this.g = g;
            this.f = g + h;
        }
    }

    private LinkedList<PathNode> findPathBetweenRooms(RoomGridCell startCell, RoomGridCell endCell)
    {
        LinkedList<PathNode> open = new LinkedList<>();
        LinkedList<PathNode> closed = new LinkedList<>();
        LinkedList<PathNode> path = new LinkedList<>();

        open.add(new PathNode(null, EnumFacing.DOWN, startCell, 0, startCell.distanceTo(endCell)));

        while (!open.isEmpty())
        {
            open.sort(Comparator.comparingDouble(PathNode::getF)); //would be more efficient to sort this as we add
            PathNode currentNode = open.removeFirst();
            if (currentNode.getCell() == endCell)
            {
                while (currentNode != null)
                {
                    path.add(currentNode);
                    currentNode = currentNode.getParent();
                }
                break;
            }

            closed.add(currentNode);

            double neighborG = currentNode.getG() + 1;
            //add each neighbor node to closed list if it connectable and not closed already
            for (EnumFacing direction : EnumFacing.HORIZONTALS)
            {
                RoomGridCell neighborCell = grid.getAdjacentCell(currentNode.getCell(), direction);
                if (neighborCell != null && neighborCell.isPopulated()/* && neighborCell.getRoom().getRoomType() != CastleRoom.RoomType.STAIRCASE*/)
                {
                    PathNode neighborNode = new PathNode(currentNode, direction.getOpposite(), neighborCell, neighborG, neighborCell.distanceTo(endCell));

                    //should really do this with .contains() but I don't feel like doing the overrides
                    boolean cellAlreadyClosed = nodeListContainsCell(closed, neighborCell);

                    if (!cellAlreadyClosed)
                    {
                        boolean cellAlreadyOpen = nodeListContainsCell(open, neighborCell);
                        if (cellAlreadyOpen)
                        {
                            PathNode openNode = getNodeThatContainsCell(open, neighborCell);
                            if (openNode.getG() > neighborG)
                            {
                                openNode.updateParent(currentNode);
                                openNode.updateG(neighborG);
                            }
                        }
                        else
                        {
                            open.add(neighborNode);
                        }
                    }
                }
            }
        }

        return path;
    }

    private boolean nodeListContainsCell(LinkedList<PathNode> nodeList, RoomGridCell cell)
    {
        return (getNodeThatContainsCell(nodeList, cell) != null);
    }

    private PathNode getNodeThatContainsCell(LinkedList<PathNode> nodeList, RoomGridCell cell)
    {
        if (!nodeList.isEmpty())
        {
            for (PathNode n : nodeList)
            {
                if (n.getCell() == cell)
                {
                    return n;
                }
            }
        }

        return null;
    }

    private RoomGridCell findNearestReachableRoom(RoomGridCell origin, ArrayList<RoomGridCell> floorRooms)
    {
        if (!floorRooms.isEmpty())
        {
            floorRooms.sort((RoomGridCell c1, RoomGridCell c2) ->
                    Double.compare(grid.distanceBetweenCells2D(origin, c1), (grid.distanceBetweenCells2D(origin, c2))));

            return floorRooms.get(0);
        }
        else
        {
            return null;
        }
    }

    private void DetermineRoofs()
    {
        for (RoomGridCell cell : grid.getSelectionListCopy())
        {
            if (cell.isPopulated() && !grid.adjacentCellIsPopulated(cell, EnumFacing.UP))
            {
                for (EnumFacing direction : EnumFacing.HORIZONTALS)
                {
                    if (!grid.adjacentCellIsPopulated(cell, direction))
                    {
                        cell.getRoom().addRoofEdge(direction);
                    }
                }
            }
        }
    }


    private void addEntranceToSide(EnumFacing side)
    {
        ArrayList<RoomGridCell> rooms = grid.getSelectionListCopy();
        rooms.removeIf(r -> r.getFloor() > 0);
        if (side == EnumFacing.NORTH)
        {
            rooms.removeIf(r -> r.getGridZ() > 0);
        }
        else if (side == EnumFacing.SOUTH)
        {
            rooms.removeIf(r -> r.getGridZ() < numSlotsZ - 1);
        }
        else if (side == EnumFacing.WEST)
        {
            rooms.removeIf(r -> r.getGridX() > 0);
        }
        else if (side == EnumFacing.EAST)
        {
            rooms.removeIf(r -> r.getGridX() < numSlotsX - 1);
        }

        if (!rooms.isEmpty())
        {
            rooms.get(random.nextInt(rooms.size())).getRoom().addDoorOnSide(side);
        }
    }

    //Translate the room's (x, z) position in the floor array to a RoomPosition enum
    private CastleRoom.RoomPosition getPositionFromIndex(int x, int z)
    {
        if (x == 0 && z == 0)
        {
            return CastleRoom.RoomPosition.TOP_LEFT;
        }
        else if (x == 0 && z < numSlotsZ - 1)
        {
            return CastleRoom.RoomPosition.MID_LEFT;
        }
        else if (x == 0)
        {
            return CastleRoom.RoomPosition.BOT_LEFT;
        }
        else if (x < numSlotsX - 1 && z == 0)
        {
            return CastleRoom.RoomPosition.TOP_MID;
        }
        else if (x < numSlotsX - 1  && z < numSlotsZ - 1)
        {
            return CastleRoom.RoomPosition.MID;
        }
        else if (x < numSlotsX - 1 )
        {
            return CastleRoom.RoomPosition.BOT_MID;
        }
        else if (z == 0)
        {
            return CastleRoom.RoomPosition.TOP_RIGHT;
        }
        else if (z < numSlotsZ - 1)
        {
            return CastleRoom.RoomPosition.MID_RIGHT;
        }
        else
        {
            return CastleRoom.RoomPosition.BOT_RIGHT;
        }
    }

    private BlockPos getRoomStart(int floor, int x, int z)
    {
        return startPos.add(x * roomSize, floor * floorHeight, z * roomSize);
    }

    private BlockPos getRoomStart(RoomGridCell selection)
    {
        int gridX = selection.getGridX();
        int floor = selection.getFloor();
        int gridZ = selection.getGridZ();
        return startPos.add(gridX * roomSize, floor * floorHeight, gridZ * roomSize);
    }

    private void addRoomHallway(int floor, int x, int z, CastleRoomHallway.Alignment alignment)
    {
        CastleRoom room = new CastleRoomHallway(getRoomStart(floor, x, z), roomSize, floorHeight, getPositionFromIndex(x, z), alignment);
        grid.addRoomAt(room, floor, x, z);
    }

    private void addStairCaseAndLanding(int stairFloor, int stairX, int stairZ)
    {
        addRoomStaircase(stairFloor, stairX, stairZ);
        addRoomLanding(stairFloor + 1, stairX, stairZ, (CastleRoomStaircase)grid.getRoomAt(stairFloor, stairX, stairZ));
    }

    private void addRoomStaircase(int floor, int x, int z)
    {
        EnumFacing doorSide = getAdjacentHallwayDirection(floor, x, z);
        CastleRoom room = new CastleRoomStaircase(getRoomStart(floor, x, z), roomSize, floorHeight, getPositionFromIndex(x, z), doorSide);
        grid.addRoomAt(room, floor, x, z);
        grid.setRoomReachable(floor, x, z);
    }

    private void addRoomLanding(int floor, int x, int z, CastleRoomStaircase stairsBelow)
    {
        CastleRoom room = new CastleRoomLanding(getRoomStart(floor, x, z), roomSize, floorHeight, getPositionFromIndex(x, z), stairsBelow);
        grid.addRoomAt(room, floor, x, z);
        grid.setRoomReachable(floor, x, z);
    }

    private int getLayerFromFloor(int floor)
    {
        return floor / floorsPerLayer;
    }
}