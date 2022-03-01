/**
 * New bounding box for collision instead of BoundingBox
 *
 * @author Almas Baimagambetov
 * @see https://www.youtube.com/watch?v=MrTW2K1i1Mk
 */
public class BBox
{
    private double minX, maxX, minY, maxY, minZ, maxZ;
    
    // Getting the diamentions
    protected BBox(double minX, double maxX, double minY, double maxY, double minZ, double maxZ){
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.minZ = minZ;
        this.maxZ = maxZ;
    }
    
    // Check if a collision is happening with another 
    protected boolean isColliding(BBox other){
        return maxX >= other.minX && minX <= other.maxX
            && maxY >= other.minY && minY <= other.maxY
            && maxZ >= other.minZ && minZ <= other.maxZ;
    }
}
