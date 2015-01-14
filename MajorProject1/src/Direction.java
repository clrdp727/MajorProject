import com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_core.*;



public class Direction
{
  private static final int THICKNESS = 2;   // of drawn line
  private CvPoint p0, p1;
  private double length;
  private double angle;   // in radians

  public Direction(CvPoint2D32f cornersA, CvPoint2D32f cornersB)
  {
    p0 = cvPoint(Math.round(cornersA.x()), Math.round(cornersA.y()));
    p1 = cvPoint(Math.round(cornersB.x()), Math.round(cornersB.y()));

    double xDist = p1.x() - p0.x();
    double yDist = p1.y() - p0.y();
if(xDist>=50 || xDist<=-50){    length = Math.sqrt((xDist*xDist) + (yDist*yDist));
      System.out.println(xDist);
    angle = Math.atan2(yDist, xDist);
}else{length=angle=0;} }  // end of Direction()


  public CvPoint getP0()
  {  return p0; }

  public CvPoint getP1()
  {  return p1; }

  public double getLength()
  {  return length; }


  public int getAngle()
  // return angle as an integer degree
  {  int angle1= (int)Math.round( Math.toDegrees(angle)); 
      System.out.println(angle1);
  return angle1;
  
  }


  public CvPoint getMid()
  {  return new CvPoint( (p0.x()+p1.x())/2, (p0.y()+p1.y())/2);  }


  public void drawArrow(IplImage im) 
  {
    // draw the direction line
    cvLine(im, p0, p1, CvScalar.BLUE, THICKNESS, CV_AA, 0);   // line type, shift

    int arrowHeadLen = (int)Math.round( length/4 );

    CvPoint arrowEnd = new CvPoint();

    // compute the coordinates of the end of the first segment of the arrow head
    arrowEnd.x( (int)Math.round(p1.x() - arrowHeadLen * Math.cos(angle + Math.PI/4)) );
    arrowEnd.y( (int)Math.round(p1.y() - arrowHeadLen * Math.sin(angle + Math.PI/4)) );

    // draw the first segment
    cvLine(im, arrowEnd, p1, CvScalar.BLUE, THICKNESS, CV_AA, 0);

    // compute the coordinates of the end of the second segment
    arrowEnd.x( (int)Math.round(p1.x() - arrowHeadLen * Math.cos(angle - Math.PI/4)) );
    arrowEnd.y( (int)Math.round(p1.y() - arrowHeadLen * Math.sin(angle - Math.PI/4)) );

    // draw the second segment
    cvLine(im, arrowEnd, p1, CvScalar.BLUE, THICKNESS, CV_AA, 0); 
  }  // end of drawArrow()


}  // end of Direction class
