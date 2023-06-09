package team.cqr.cqrepoured.world.structure.generation.generators.castleparts;

import net.minecraft.core.Direction;

import java.util.Objects;

public class RoomGridPosition {
	private int floor;
	private int x;
	private int z;

	public RoomGridPosition(int floor, int x, int z) {
		this.floor = floor;
		this.x = x;
		this.z = z;
	}

	public RoomGridPosition(RoomGridPosition pos) {
		this.floor = pos.floor;
		this.x = pos.x;
		this.z = pos.z;
	}

	public int getFloor() {
		return this.floor;
	}

	public int getX() {
		return this.x;
	}

	public int getZ() {
		return this.z;
	}

	public void setFloor(int floor) {
		this.floor = floor;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public RoomGridPosition move(Direction direction) {
		return this.move(direction, 1);
	}

	public RoomGridPosition move(Direction direction, int distance) {
		int floor = this.getFloor();
		int x = this.getX();
		int z = this.getZ();

		for (int i = 0; i < distance; i++) {
			switch (direction) {
			case NORTH:
				z--;
				break;
			case SOUTH:
				z++;
				break;
			case WEST:
				x--;
				break;
			case EAST:
				x++;
				break;
			case UP:
				floor++;
				break;
			case DOWN:
				floor--;
				break;
			default:
				break;
			}
		}

		return new RoomGridPosition(floor, x, z);
	}

	@Override
	public String toString() {
		return String.format("RoomGridPosition{floor=%d, x=%d, z=%d}", this.floor, this.x, this.z);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof RoomGridPosition)) {
			return false;
		}
		RoomGridPosition position = (RoomGridPosition) obj;
		return (this.floor == position.floor && this.x == position.x && this.z == position.z);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.floor, this.x, this.z);
	}
}
