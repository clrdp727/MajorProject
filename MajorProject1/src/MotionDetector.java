
import java.awt.*;

import com.googlecode.javacv.*;
import com.googlecode.javacv.cpp.*;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;



public class MotionDetector
{
  private static final int MIN_PIXELS = 1700;   
          // minimum number of non-black pixels needed for COG calculation
  private static final int LOW_THRESHOLD = 64;

  private IplImage prevImg, currImg, diffImg;     // grayscale images (diffImg is bi-level)
  private Dimension imDim = null;       // image dimensions
  private Point cogPoint = null;        // center-of-gravity (COG) coordinate
  static ColorChanging c1=new ColorChanging();

  public MotionDetector(IplImage firstFrame)
  {
    if (firstFrame == null) {
      System.out.println("No frame to initialize motion detector");
      System.exit(1);
    }

    imDim = new Dimension( firstFrame.width(), firstFrame.height() );
    prevImg = convertFrame(firstFrame);
    currImg = null; 
    diffImg = IplImage.create(prevImg.width(), prevImg.height(), IPL_DEPTH_8U, 1);
  }  // end of MotionDetector()



  private IplImage convertFrame(IplImage img)
  /* Conversion involves: blurring, converting color to grayscale, 
     and equalization */
  {
    // blur image to get reduce camera noise 
    cvSmooth(img, img, CV_BLUR, 3);  

    // convert to grayscale
    IplImage grayImg = IplImage.create(img.width(), img.height(), IPL_DEPTH_8U, 1);
//    cvCvtColor(img, grayImg, CV_BGR2GRAY);  
    cvCvtColor(img, grayImg, CV_BGR2GRAY);  

	cvEqualizeHist(grayImg, grayImg);       // spread out the grayscale range 

    return grayImg;
  }  // end of convertFrame()



  public void calcMove(IplImage currFrame)
  // use a new image to update the COG point
  {
    if (currFrame == null) {
      System.out.println("Current frame is null");
      return;
    }

    if (currImg != null) // store old current as the previous image
      prevImg = currImg;

    currImg = convertFrame(currFrame);

    cvAbsDiff(currImg, prevImg, diffImg);    
    cvThreshold(diffImg, diffImg, LOW_THRESHOLD, 255, CV_THRESH_BINARY);

    Point pt = findCOG(diffImg);
    if (pt != null)     // only update COG point if there is a new point
      cogPoint = pt;
  }  // end of calcMove()


  public Point getCOG()
  {  return cogPoint;  }

  public IplImage getCurrImg()
  {  return currImg;  }

  public IplImage getDiffImg()
  {  return diffImg;  }

  public Dimension getSize()
  {  return imDim;  }


int xCenter=100,yCenter=100;
  private Point findCOG(IplImage diffImg)
  /*  If there are enough non-black pixels in the difference image
      (non-black means a difference, i.e. movement), then calculate the moments,
      and use them to calculate the (x,y) center of the white areas.
      These values are returned as a Point object. */
  {
    Point pt = null;

    int numPixels = cvCountNonZero(diffImg);   // non-zero (non-black) means motion
    if (numPixels > MIN_PIXELS) {
      CvMoments moments = new CvMoments();
      cvMoments(diffImg, moments, 1);    // 1 == treat image as binary (0,255) --> (0,1)
      double m00 = cvGetSpatialMoment(moments, 0, 0) ; 
      double m10 = cvGetSpatialMoment(moments, 1, 0) ; 
      double m01 = cvGetSpatialMoment(moments, 0, 1); 

      if (m00 != 0) {   // create COG Point
        xCenter = (int) Math.round(m10/m00); 
        yCenter = (int) Math.round(m01/m00);
        pt = new Point(xCenter, yCenter);
        c1.changeColor(xCenter%250, yCenter%250);
        System.out.println("x="+xCenter+"y="+yCenter);
      }
    }
    return pt;
  }  // end of findCOG()

 

  public static void main(String[] args) throws Exception
          
  {
    System.out.println("Initializing frame grabber...");
    OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(CV_CAP_ANY);
    grabber.start();
    c1.setVisible(true);
    MotionDetector md = new MotionDetector( grabber.grab() );

    Dimension imgDim = md.getSize();
    IplImage imgWithCOG = IplImage.create(imgDim.width, imgDim.height, IPL_DEPTH_8U, 1); 
    IplImage ycrcbimage= cvCreateImage(cvSize(640,480),IPL_DEPTH_8U,3);
    IplImage binimage= cvCreateImage(cvSize(640,480),IPL_DEPTH_8U,1);
    // canvases for the image+COG and difference images
    CanvasFrame cogCanvas = new CanvasFrame("Camera + COG");
    cogCanvas.setLocation(0, 0);

    CanvasFrame diffCanvas = new CanvasFrame("Difference");
    diffCanvas.setLocation(imgDim.width+5, 0);

    // display grayscale+COG and difference images, until a window is closed
    while (cogCanvas.isVisible() && diffCanvas.isVisible())  {
      md.calcMove( grabber.grab() );

      // show current grayscale image with COG drawn onto it
      cvCopy(md.getCurrImg(), imgWithCOG);
      Point cogPoint = md.getCOG();
      if (cogPoint != null)
        cvCircle(imgWithCOG, cvPoint(cogPoint.x, cogPoint.y), 8,      // radius of circle
                  CvScalar.WHITE, CV_FILLED, CV_AA, 0);     // line type, shift
        cvFlip(imgWithCOG, imgWithCOG, 1);
        cvFlip(md.getDiffImg(), md.getDiffImg(), 1);
        
      cogCanvas.showImage(imgWithCOG);   
      diffCanvas.showImage(md.getDiffImg());   // show difference image
    }

    grabber.stop();
    cogCanvas.dispose();
    diffCanvas.dispose();
  }  // end of main()
}  // end of MotionDetector class
