package javaapplication3;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacpp.Pointer;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.CV_FILLED;
import static com.googlecode.javacv.cpp.opencv_core.CV_RGB;
import com.googlecode.javacv.cpp.opencv_core.CvScalar;
import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_core.cvCreateImage;
import static com.googlecode.javacv.cpp.opencv_core.cvDrawContours;
import static com.googlecode.javacv.cpp.opencv_core.cvFlip;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSize;
import static com.googlecode.javacv.cpp.opencv_core.cvInRangeS;
import static com.googlecode.javacv.cpp.opencv_core.cvPoint;
import static com.googlecode.javacv.cpp.opencv_core.cvReleaseImage;
import static com.googlecode.javacv.cpp.opencv_core.cvSize;
import com.googlecode.javacv.cpp.opencv_highgui;
import static com.googlecode.javacv.cpp.opencv_highgui.CV_CAP_ANY;
import com.googlecode.javacv.cpp.opencv_highgui.CvCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvCreateCameraCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvDestroyWindow;
import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvNamedWindow;
import static com.googlecode.javacv.cpp.opencv_highgui.cvQueryFrame;
import static com.googlecode.javacv.cpp.opencv_highgui.cvReleaseCapture;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvShowImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvWaitKey;
import com.googlecode.javacv.cpp.opencv_imgproc;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2GRAY;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2HSV;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_BGR2YCrCb;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CHAIN_APPROX_SIMPLE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_CLOCKWISE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_COUNTER_CLOCKWISE;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_MOP_OPEN;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_POLY_APPROX_DP;
import static com.googlecode.javacv.cpp.opencv_imgproc.CV_RETR_LIST;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvApproxPoly;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvConvexHull2;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvConvexityDefects;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvCvtColor;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvFindContours;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetCentralMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvGetSpatialMoment;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMinAreaRect2;
import static com.googlecode.javacv.cpp.opencv_imgproc.cvMoments;
import java.awt.AWTException;
import java.awt.Font;
import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
/**
 *
 *
 */
public class DetectingOnlyFinger extends javax.swing.JFrame {
  private opencv_core.CvMemStorage contourStorage, approxStorage, hullStorage, defectsStorage;
  private static final float SMALLEST_AREA =  600.0f;    // was 100.0f;
  private static final int MAX_POINTS = 20;   // max number of points stored in an array

  // used for simiplifying the defects list
  private static final int MIN_FINGER_DEPTH = 20;
  private static final int MAX_FINGER_ANGLE = 60;   // degrees

  // angle ranges of thumb and index finger of the left hand relative to its COG
  private static final int MIN_THUMB = 120;
  private static final int MAX_THUMB = 200;

  private static final int MIN_INDEX = 60;
  private static final int MAX_INDEX = 120;
  private IplImage scaleImg;     // for resizing the webcam image
  private IplImage hsvImg;       // HSV version of webcam image
  private IplImage imgThreshed;  // threshold for HSV settings

  private Font msgFont;

  // hand details
  private Point cogPt;           // center of gravity (COG) of contour
  private int contourAxisAngle;      
             // contour's main axis angle relative to the horizontal (in degrees)

  // defects data for the hand contour
  private Point[] tipPts, foldPts;    
  private float[] depths;
  private ArrayList<Point> fingerTips;

  // finger identifications
  private ArrayList<FingerName> namedFingers;

    /**
     * Creates new form WebcamInitiation
     */
    public DetectingOnlyFinger() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton3.setText("Webcam");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(152, 152, 152)
                .addComponent(jButton3)
                .addContainerGap(175, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addComponent(jButton3)
                .addContainerGap(188, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
    contourStorage = opencv_core.CvMemStorage.create();
    approxStorage = opencv_core.CvMemStorage.create();
    hullStorage = opencv_core.CvMemStorage.create();
    defectsStorage = opencv_core.CvMemStorage.create();
    cogPt = new Point();
    fingerTips = new ArrayList<Point>();
    namedFingers = new ArrayList<FingerName>();

    tipPts = new Point[MAX_POINTS];   // coords of the finger tips
    foldPts = new Point[MAX_POINTS];  // coords of the skin folds between fingers
    depths = new float[MAX_POINTS];   // distances from tips to folds

    CvCapture capture= cvCreateCameraCapture(CV_CAP_ANY);
        
        IplImage frame,template;
        
    IplImage ycrcbimage= cvCreateImage(cvSize(640,480),IPL_DEPTH_8U,3);
    IplImage binimage= cvCreateImage(cvSize(640,480),IPL_DEPTH_8U,1);
     cvNamedWindow("Webcam",NORMAL);
     
     for(;;)
     {
        frame= cvQueryFrame(capture);         
        cvCvtColor(frame,ycrcbimage,CV_BGR2YCrCb);
        CvScalar minc,maxc; 
        minc= new CvScalar(54,131,110,0);
        maxc=new CvScalar(163,157,135,0);        
        cvInRangeS(ycrcbimage,minc,maxc,binimage);
        opencv_imgproc.cvMorphologyEx(binimage, binimage, null, null, CV_MOP_OPEN, 1);
        cvFlip(ycrcbimage, ycrcbimage, 1);    
        opencv_core.CvSeq bigContour = findBiggestContour(binimage);
        if (bigContour == null) 
             System.out.println("galti hui");
        else
        {
                extractContourInfo(bigContour, 2);
                findFingerTips(bigContour, 2);
                if(fingerTips.size()==5){
                nameFingers(cogPt, contourAxisAngle, fingerTips);                
                }
        }
        cvFlip(binimage, binimage, 1);
        cvShowImage("Webcam",ycrcbimage);
        cvShowImage("Binary",binimage);
        char c= (char) cvWaitKey(30);
        if (c==27) break;
     }  
        cvReleaseCapture(capture);
        cvDestroyWindow("Webcam");
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DetectingOnlyFinger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DetectingOnlyFinger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DetectingOnlyFinger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DetectingOnlyFinger.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DetectingOnlyFinger().setVisible(true);
            }
        });
    }
  private opencv_core.CvSeq findBiggestContour(IplImage imgThreshed)
  // return the largest contour in the threshold image
  { 
    opencv_core.CvSeq bigContour = null;

    // generate all the contours in the threshold image as a list
    opencv_core.CvSeq contours = new opencv_core.CvSeq(null);
    opencv_core.CvSeq contours2 = new opencv_core.CvSeq(null);
    cvFindContours(imgThreshed, contourStorage, contours, Loader.sizeof(opencv_core.CvContour.class),
                                                       CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
    cvFindContours(imgThreshed, contourStorage, contours2, Loader.sizeof(opencv_core.CvContour.class),
                                                       CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE);
    // find the largest contour in the list based on bounded box size
    float maxArea = SMALLEST_AREA;
    opencv_core.CvBox2D maxBox = null;
    while (contours != null && !contours.isNull()) {
      if (contours.elem_size() > 0) {
        opencv_core.CvBox2D box = cvMinAreaRect2(contours, contourStorage);
        
        if (box != null) {
          opencv_core.CvSize2D32f size = box.size();
          float area = size.width() * size.height();
          if (area > maxArea) {
            maxArea = area;
            bigContour = contours;
//              System.out.println("hi bhai "+box);
          }
        }
      }      
      contours = contours.h_next();
    }
//      System.out.println(maxArea);
    while (contours2 != null && !contours2.isNull()) {
      if (contours2.elem_size() > 0) {
        opencv_core.CvBox2D box = cvMinAreaRect2(contours2, contourStorage);        
        if (box != null) {
          opencv_core.CvSize2D32f size = box.size();
          float area = size.width() * size.height();
          if (area < maxArea) {
                 cvDrawContours(imgThreshed,contours2, CV_RGB(0,0,0),CV_RGB(0,0,0),0, CV_FILLED,8, cvPoint(0,0));          
          } else if(area==maxArea) bigContour=contours2;
        }
      }
      contours2 = contours2.h_next();
    }    
return bigContour;
  }  // end of findBiggestContour()

  
  private void extractContourInfo(opencv_core.CvSeq bigContour, int scale)
  {
    opencv_imgproc.CvMoments moments = new opencv_imgproc.CvMoments();
    cvMoments(bigContour, moments, 1);     // CvSeq is a subclass of CvArr

    // center of gravity
    double m00 = cvGetSpatialMoment(moments, 0, 0) ;
    double m10 = cvGetSpatialMoment(moments, 1, 0) ;
    double m01 = cvGetSpatialMoment(moments, 0, 1);

    if (m00 != 0) {   // calculate center
      int xCenter = (int) Math.round(m10/m00)*scale;
      int yCenter = (int) Math.round(m01/m00)*scale;
//      System.out.println("x="+xCenter+" y="+yCenter);
      cogPt.setLocation(xCenter, yCenter);
    }

    double m11 = cvGetCentralMoment(moments, 1, 1);
    double m20 = cvGetCentralMoment(moments, 2, 0);
    double m02 = cvGetCentralMoment(moments, 0, 2);
    contourAxisAngle = calculateTilt(m11, m20, m02);
//      System.out.println(contourAxisAngle);
    if (fingerTips.size() > 0) {
      int yTotal = 0;
      for(Point pt : fingerTips)
        yTotal += pt.y;
//        System.out.println(yTotal);
      int avgYFinger = yTotal/fingerTips.size();
      if (avgYFinger > cogPt.y)   // fingers below COG
        contourAxisAngle += 180;
    }
    contourAxisAngle = 180 - contourAxisAngle;   

  }  // end of extractContourInfo()

  private int calculateTilt(double m11, double m20, double m02)
  {
    double diff = m20 - m02;
    if (diff == 0) {
      if (m11 == 0)
        return 0;
      else if (m11 > 0)
        return 45;
      else   // m11 < 0
        return -45;
    }

    double theta = 0.5 * Math.atan2(2*m11, diff);
    int tilt = (int) Math.round( Math.toDegrees(theta));

    if ((diff > 0) && (m11 == 0))
      return 0;
    else if ((diff < 0) && (m11 == 0))
      return -90;
    else if ((diff > 0) && (m11 > 0))  // 0 to 45 degrees
      return tilt;
    else if ((diff > 0) && (m11 < 0))  // -45 to 0
      return (180 + tilt);   // change to counter-clockwise angle measure
    else if ((diff < 0) && (m11 > 0))   // 45 to 90
      return tilt;
    else if ((diff < 0) && (m11 < 0))   // -90 to -45
      return (180 + tilt);  // change to counter-clockwise angle measure

    System.out.println("Error in moments for tilt angle");
    return 0;
  }  // end of calculateTilt()
  private void findFingerTips(opencv_core.CvSeq bigContour, int scale)
  {
    opencv_core.CvSeq approxContour = cvApproxPoly(bigContour, Loader.sizeof(opencv_core.CvContour.class), 
                                                     approxStorage, CV_POLY_APPROX_DP, 3, 1);
       // reduce number of points in the contour

    opencv_core.CvSeq hullSeq = cvConvexHull2(approxContour, hullStorage, CV_COUNTER_CLOCKWISE, 0);
       // find the convex hull around the contour

    opencv_core.CvSeq defects = cvConvexityDefects(approxContour, hullSeq, defectsStorage);
       // find the defect differences between the contour and hull
    int defectsTotal = defects.total();
//      System.out.println(defectsTotal);
    if (defectsTotal > MAX_POINTS) {
      System.out.println("Only processing " + MAX_POINTS + " defect points");
      defectsTotal = MAX_POINTS;
    }

    // copy defect information from defects sequence into arrays
    for (int i = 0; i < defectsTotal; i++) {
      Pointer pntr = cvGetSeqElem(defects, i);
      opencv_imgproc.CvConvexityDefect cdf = new opencv_imgproc.CvConvexityDefect(pntr);

      opencv_core.CvPoint startPt = cdf.start();
      tipPts[i] = new Point( (int)Math.round(startPt.x()*scale),
                                (int)Math.round(startPt.y()*scale));
        // an array containing the coordinates of the finger tips

      opencv_core.CvPoint endPt = cdf.end();
      opencv_core.CvPoint depthPt = cdf.depth_point();
      foldPts[i] = new Point( (int)Math.round(depthPt.x()*scale),
                                (int)Math.round(depthPt.y()*scale));
          // an array containing the coordinates of the skin fold between fingers
      depths[i] = cdf.depth()*scale;
           // an array containing the distances from tips to folds
    }

    reduceTips(defectsTotal, tipPts, foldPts, depths);
  }  // end of findFingerTips()



  private void reduceTips(int numPoints, Point[] tipPts, Point[] foldPts, float[] depths)
  {
    fingerTips.clear();

    for (int i=0; i < numPoints; i++) {
      if (depths[i] < MIN_FINGER_DEPTH)    // defect too shallow
        continue;

      // look at fold points on either side of a tip
      int pdx = (i == 0) ? (numPoints-1) : (i - 1);   // predecessor of i
      int sdx = (i == numPoints-1) ? 0 : (i + 1);     // successor of i
      int angle = angleBetween(tipPts[i], foldPts[pdx], foldPts[sdx]);
//        System.out.println(angle);
      if (angle >= MAX_FINGER_ANGLE)      // angle between finger and folds too wide
        continue;
      // this point probably is a finger tip, so add to list
      fingerTips.add(tipPts[i]);
    }
//     System.out.println("No. of finger tips: " + fingerTips.size());
  }  // end of reduceTips()

  private int angleBetween(Point tip, Point next, Point prev)
  // calulate the angle between the tip and its neigbouring folds (in integer degrees)
  { 
    return Math.abs( (int)Math.round( 
                        Math.toDegrees(
                              Math.atan2(next.x - tip.x, next.y - tip.y) -
                              Math.atan2(prev.x - tip.x, prev.y - tip.y)) ));
  }

  private void nameFingers(Point cogPt, int contourAxisAngle, ArrayList<Point> fingerTips)
  { // reset all named fingers to unknown
    namedFingers.clear();
    for (int i=0; i < fingerTips.size(); i++)
      namedFingers.add(FingerName.UNKNOWN);

    labelThumbIndex(fingerTips, namedFingers);

//     printFingers("named fingers", namedFingers);
    labelUnknowns(namedFingers);
    printFingers("revised named fingers", namedFingers);
  }  // end of nameFingers()



  private void labelThumbIndex(ArrayList<Point> fingerTips,ArrayList<FingerName> nms)
  // attempt to label the thumb and index fingers of the hand
  { 
    boolean foundThumb = false;
    boolean foundIndex = false;
    int i = fingerTips.size()-1;
//        System.out.println("->");
    while ((i >= 0)) {
      int angle = angleToCOG(fingerTips.get(i), cogPt, contourAxisAngle);
//        System.out.println(angle);
      // check for thumb
      if ((angle <=  MAX_THUMB) && (angle > MIN_THUMB) && !foundThumb) {
//      if ((angle <=  50) && (angle > 0) && !foundThumb) {
        nms.set(i, FingerName.THUMB);
        foundThumb = true;
      }

      // check for index
      if ((angle <= MAX_INDEX) && (angle > MIN_INDEX) && !foundIndex) {
//      if ((angle <=50) && (angle > 90) && !foundIndex) {
        nms.set(i, FingerName.INDEX);
        foundIndex = true;
      }
      i--;
   }
  }  // end of labelThumbIndex()




  private int angleToCOG(Point tipPt, Point cogPt, int contourAxisAngle)
  /* calculate angle of tip relative to the COG, remembering to add the
     hand contour angle so that the hand is orientated straight up */
  {
    int yOffset = cogPt.y - tipPt.y;    // make y positive up screen
    int xOffset = tipPt.x - cogPt.x;
    // Point offsetPt = new Point(xOffset, yOffset);

    double theta = Math.atan2(yOffset, xOffset);
    int angleTip = (int) Math.round( Math.toDegrees(theta));
    int offsetAngleTip = angleTip + (90 - contourAxisAngle);
             // this addition ensures that the hand is orientated straight up
    return offsetAngleTip;
  }  // end of angleToCOG()



  private void printFingers(String title, ArrayList<FingerName> nms)
  { System.out.println(title);
    for(FingerName name : nms)
      System.out.println("  " + name);
  }
  private void labelUnknowns(ArrayList<FingerName> nms)
  // attempt to label all the unknown fingers in the list
  { 
    // find first named finger
    int i = 0;
    while ((i < nms.size()) && (nms.get(i) == FingerName.UNKNOWN))
      i++;
    if (i == nms.size())   // no named fingers found, so give up
      return;

    FingerName name = nms.get(i);
    labelPrev(nms, i, name);    // fill-in backwards
    labelFwd(nms, i, name);    // fill-in forwards
  }  // end of labelUnknowns()



  private void labelPrev(ArrayList<FingerName> nms, int i, FingerName name)
  // move backwards through fingers list labelling unknown fingers
  { 
    i--;
    while ((i >= 0) && (name != FingerName.UNKNOWN)){
      if (nms.get(i) == FingerName.UNKNOWN) {   // unknown finger
        name = name.getPrev();
        if (!usedName(nms, name))
          nms.set(i, name);
      }
      else   // finger is named already
        name = nms.get(i);
      i--;
    }
  }  // end of labelPrev()



  private void labelFwd(ArrayList<FingerName> nms, int i, FingerName name)
  // move forward through fingers list labelling unknown fingers
  { 
    i++;
    while ((i < nms.size()) && (name != FingerName.UNKNOWN)) {
      if (nms.get(i) == FingerName.UNKNOWN) {  // unknown finger
        name = name.getNext();
        if (!usedName(nms, name))
          nms.set(i, name);
      }
      else    // finger is named already
        name = nms.get(i);
      i++;
    }
  }  // end of labelFwd()



  private boolean usedName(ArrayList<FingerName> nms, FingerName name)
  // does the fingers list contain name already?
  { 
    for(FingerName fn : nms)
      if (fn == name)
        return true;
    return false;
  }  // end of usedName()


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    // End of variables declaration//GEN-END:variables
}
