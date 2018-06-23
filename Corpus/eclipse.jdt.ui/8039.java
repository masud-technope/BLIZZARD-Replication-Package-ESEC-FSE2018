package p;

public enum Direction implements  {

    // note semicolon here
    NORTH() {
    }
    , // note semicolon here
    SOUTH() {
    }
    , // note semicolon here
    EAST() {
    }
    , // note semicolon here
    WEST() {
    }
    ;

    public Direction opposite() {
        switch(this) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
            default:
                return null;
        }
    }
}
