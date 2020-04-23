package me.shedaniel.forge.math;

public class Rectangle implements Cloneable {
    public int x;
    public int y;
    public int width;
    public int height;
    
    public Rectangle() {
        this(0, 0, 0, 0);
    }
    
    public Rectangle(Rectangle r) {
        this(r.x, r.y, r.width, r.height);
    }
    
    public Rectangle(int width, int height) {
        this(0, 0, width, height);
    }
    
    public Rectangle(Point p, Dimension d) {
        this(p.x, p.y, d.width, d.height);
    }
    
    public Rectangle(Point p) {
        this(p.x, p.y, 0, 0);
    }
    
    public Rectangle(Dimension d) {
        this(0, 0, d.width, d.height);
    }
    
    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public int getX() {
        return x;
    }
    
    public int getMinX() {
        return x;
    }
    
    public int getMaxX() {
        return x + width;
    }
    
    public int getCenterX() {
        return x + width / 2;
    }
    
    public int getY() {
        return y;
    }
    
    public int getMinY() {
        return y;
    }
    
    public int getMaxY() {
        return y + height;
    }
    
    public int getCenterY() {
        return y + height / 2;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }
    
    public void setBounds(int x, int y, int width, int height) {
        reshape(x, y, width, height);
    }
    
    @Deprecated
    public void reshape(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    public Point getLocation() {
        return new Point(x, y);
    }
    
    public void setLocation(Point p) {
        setLocation(p.x, p.y);
    }
    
    public void setLocation(int x, int y) {
        move(x, y);
    }
    
    @Deprecated
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }
    
    @Override
    public Rectangle clone() {
        return getBounds();
    }
    
    public Dimension getSize() {
        return new Dimension(width, height);
    }
    
    public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }
    
    public void setSize(int width, int height) {
        resize(width, height);
    }
    
    @Deprecated
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public boolean contains(Point p) {
        return contains(p.x, p.y);
    }
    
    public boolean contains(int x, int y) {
        return inside(x, y);
    }
    
    public boolean contains(double x, double y) {
        return inside((int) x, (int) y);
    }
    
    public boolean contains(Rectangle r) {
        return contains(r.x, r.y, r.width, r.height);
    }
    
    public boolean contains(int X, int Y, int W, int H) {
        int w = this.width;
        int h = this.height;
        if ((w | h | W | H) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if any dimension is zero, tests below must return false...
        int x = this.x;
        int y = this.y;
        if (X < x || Y < y) {
            return false;
        }
        w += x;
        W += X;
        if (W <= X) {
            // X+W overflowed or W was zero, return false if...
            // either original w or W was zero or
            // x+w did not overflow or
            // the overflowed x+w is smaller than the overflowed X+W
            if (w >= x || W > w)
                return false;
        } else {
            // X+W did not overflow and W was not zero, return false if...
            // original w was zero or
            // x+w did not overflow and x+w is smaller than X+W
            if (w >= x && W > w)
                return false;
        }
        h += y;
        H += Y;
        if (H <= Y) {
            return h < y && H <= h;
        } else {
            return h < y || H <= h;
        }
    }
    
    @Deprecated
    public boolean inside(int X, int Y) {
        int w = this.width;
        int h = this.height;
        if ((w | h) < 0) {
            // At least one of the dimensions is negative...
            return false;
        }
        // Note: if either dimension is zero, tests below must return false...
        int x = this.x;
        int y = this.y;
        if (X < x || Y < y) {
            return false;
        }
        w += x;
        h += y;
        //    overflow || intersect
        return ((w < x || w > X) && (h < y || h > Y));
    }
    
    public boolean intersects(Rectangle r) {
        int tw = this.width;
        int th = this.height;
        int rw = r.width;
        int rh = r.height;
        if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
            return false;
        }
        int tx = this.x;
        int ty = this.y;
        int rx = r.x;
        int ry = r.y;
        rw += rx;
        rh += ry;
        tw += tx;
        th += ty;
        //      overflow || intersect
        return ((rw < rx || rw > tx) && (rh < ry || rh > ty) && (tw < tx || tw > rx) && (th < ty || th > ry));
    }
    
    public Rectangle intersection(Rectangle r) {
        int tx1 = this.x;
        int ty1 = this.y;
        int rx1 = r.x;
        int ry1 = r.y;
        long tx2 = tx1;
        tx2 += this.width;
        long ty2 = ty1;
        ty2 += this.height;
        long rx2 = rx1;
        rx2 += r.width;
        long ry2 = ry1;
        ry2 += r.height;
        if (tx1 < rx1)
            tx1 = rx1;
        if (ty1 < ry1)
            ty1 = ry1;
        if (tx2 > rx2)
            tx2 = rx2;
        if (ty2 > ry2)
            ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never overflow (they will never be
        // larger than the smallest of the two source w,h)
        // they might underflow, though...
        if (tx2 < Integer.MIN_VALUE)
            tx2 = Integer.MIN_VALUE;
        if (ty2 < Integer.MIN_VALUE)
            ty2 = Integer.MIN_VALUE;
        return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
    }
    
    public Rectangle union(Rectangle r) {
        long tx2 = this.width;
        long ty2 = this.height;
        if ((tx2 | ty2) < 0) {
            // This rectangle has negative dimensions...
            // If r has non-negative dimensions then it is the answer.
            // If r is non-existant (has a negative dimension), then both
            // are non-existant and we can return any non-existant rectangle
            // as an answer.  Thus, returning r meets that criterion.
            // Either way, r is our answer.
            return new Rectangle(r);
        }
        long rx2 = r.width;
        long ry2 = r.height;
        if ((rx2 | ry2) < 0) {
            return new Rectangle(this);
        }
        int tx1 = this.x;
        int ty1 = this.y;
        tx2 += tx1;
        ty2 += ty1;
        int rx1 = r.x;
        int ry1 = r.y;
        rx2 += rx1;
        ry2 += ry1;
        if (tx1 > rx1)
            tx1 = rx1;
        if (ty1 > ry1)
            ty1 = ry1;
        if (tx2 < rx2)
            tx2 = rx2;
        if (ty2 < ry2)
            ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never underflow since both original rectangles
        // were already proven to be non-empty
        // they might overflow, though...
        if (tx2 > Integer.MAX_VALUE)
            tx2 = Integer.MAX_VALUE;
        if (ty2 > Integer.MAX_VALUE)
            ty2 = Integer.MAX_VALUE;
        return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
    }
    
    public void add(int newx, int newy) {
        if ((width | height) < 0) {
            this.x = newx;
            this.y = newy;
            this.width = this.height = 0;
            return;
        }
        int x1 = this.x;
        int y1 = this.y;
        long x2 = this.width;
        long y2 = this.height;
        x2 += x1;
        y2 += y1;
        if (x1 > newx)
            x1 = newx;
        if (y1 > newy)
            y1 = newy;
        if (x2 < newx)
            x2 = newx;
        if (y2 < newy)
            y2 = newy;
        x2 -= x1;
        y2 -= y1;
        if (x2 > Integer.MAX_VALUE)
            x2 = Integer.MAX_VALUE;
        if (y2 > Integer.MAX_VALUE)
            y2 = Integer.MAX_VALUE;
        reshape(x1, y1, (int) x2, (int) y2);
    }
    
    public void add(Point pt) {
        add(pt.x, pt.y);
    }
    
    public void add(Rectangle r) {
        long tx2 = this.width;
        long ty2 = this.height;
        if ((tx2 | ty2) < 0) {
            reshape(r.x, r.y, r.width, r.height);
        }
        long rx2 = r.width;
        long ry2 = r.height;
        if ((rx2 | ry2) < 0) {
            return;
        }
        int tx1 = this.x;
        int ty1 = this.y;
        tx2 += tx1;
        ty2 += ty1;
        int rx1 = r.x;
        int ry1 = r.y;
        rx2 += rx1;
        ry2 += ry1;
        if (tx1 > rx1)
            tx1 = rx1;
        if (ty1 > ry1)
            ty1 = ry1;
        if (tx2 < rx2)
            tx2 = rx2;
        if (ty2 < ry2)
            ty2 = ry2;
        tx2 -= tx1;
        ty2 -= ty1;
        // tx2,ty2 will never underflow since both original
        // rectangles were non-empty
        // they might overflow, though...
        if (tx2 > Integer.MAX_VALUE)
            tx2 = Integer.MAX_VALUE;
        if (ty2 > Integer.MAX_VALUE)
            ty2 = Integer.MAX_VALUE;
        reshape(tx1, ty1, (int) tx2, (int) ty2);
    }
    
    public void grow(int h, int v) {
        long x0 = this.x;
        long y0 = this.y;
        long x1 = this.width;
        long y1 = this.height;
        x1 += x0;
        y1 += y0;
        
        x0 -= h;
        y0 -= v;
        x1 += h;
        y1 += v;
        
        if (x1 < x0) {
            // Non-existant in X direction
            // Final width must remain negative so subtract x0 before
            // it is clipped so that we avoid the risk that the clipping
            // of x0 will reverse the ordering of x0 and x1.
            x1 -= x0;
            if (x1 < Integer.MIN_VALUE)
                x1 = Integer.MIN_VALUE;
            if (x0 < Integer.MIN_VALUE)
                x0 = Integer.MIN_VALUE;
            else if (x0 > Integer.MAX_VALUE)
                x0 = Integer.MAX_VALUE;
        } else { // (x1 >= x0)
            // Clip x0 before we subtract it from x1 in case the clipping
            // affects the representable area of the rectangle.
            if (x0 < Integer.MIN_VALUE)
                x0 = Integer.MIN_VALUE;
            else if (x0 > Integer.MAX_VALUE)
                x0 = Integer.MAX_VALUE;
            x1 -= x0;
            // The only way x1 can be negative now is if we clipped
            // x0 against MIN and x1 is less than MIN - in which case
            // we want to leave the width negative since the result
            // did not intersect the representable area.
            if (x1 < Integer.MIN_VALUE)
                x1 = Integer.MIN_VALUE;
            else if (x1 > Integer.MAX_VALUE)
                x1 = Integer.MAX_VALUE;
        }
        
        if (y1 < y0) {
            // Non-existant in Y direction
            y1 -= y0;
            if (y1 < Integer.MIN_VALUE)
                y1 = Integer.MIN_VALUE;
            if (y0 < Integer.MIN_VALUE)
                y0 = Integer.MIN_VALUE;
            else if (y0 > Integer.MAX_VALUE)
                y0 = Integer.MAX_VALUE;
        } else { // (y1 >= y0)
            if (y0 < Integer.MIN_VALUE)
                y0 = Integer.MIN_VALUE;
            else if (y0 > Integer.MAX_VALUE)
                y0 = Integer.MAX_VALUE;
            y1 -= y0;
            if (y1 < Integer.MIN_VALUE)
                y1 = Integer.MIN_VALUE;
            else if (y1 > Integer.MAX_VALUE)
                y1 = Integer.MAX_VALUE;
        }
        
        reshape((int) x0, (int) y0, (int) x1, (int) y1);
    }
    
    public boolean isEmpty() {
        return (width <= 0) || (height <= 0);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rectangle) {
            Rectangle r = (Rectangle) obj;
            return ((x == r.x) && (y == r.y) && (width == r.width) && (height == r.height));
        }
        return super.equals(obj);
    }
    
    @Override
    public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + ",width=" + width + ",height=" + height + "]";
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + width;
        result = 31 * result + height;
        return result;
    }
}