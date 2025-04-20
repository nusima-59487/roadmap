package me.nusimucat.roadmap;

public class Segment {
    private int segmentId;
    private Node startNode;
    private Node endNode;
    
    public int getId() {
        return this.segmentId;     
    }

    public static Segment fromDatabase(int associatedSegmentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'fromDatabase'");
    }
    
}
