import objectdata.*;
import raster.ZBufferAlgorithm;
import renderer.*;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * CANVAS MAIN CLASS Author: Pavel Švarc
 */
public class Canvas {

    private final JFrame frame;
    private final JPanel panel;
    private BufferedImage img;
    private final ColorShader cs = new ColorShader();

    //object creating
    private final Cube c = new Cube();
    private final Pyramid p = new Pyramid();
    private final Axes a = new Axes();
    private int intTypeFlat = 1;
    private Flat f = new Flat(intTypeFlat);

    private final ZBufferAlgorithm zb;
    private final RasterizerTriangles rt;
    private final RendererTriangle rendererTriangle;
    private final RasterizerLines rl;
    private final RendererLine rendererLine;

    //setting matrix
    private Mat4 identiti = new Mat4Identity();
    private Mat4 identitiFixed = new Mat4Identity();
    private Mat4 viewMat;
    private Mat4 transl, translFixed;
    private Mat4 persp, orto;
    private Camera cam;

    //helping variables
    private int view=0;
    private int mode=0;
    private int showCube, showPyramid, showFlat, showAxes, fixedCube= 0;
    private int previousX, previousY;

    public Canvas(final int width, final int height){

        frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.setTitle("UHK FIM PGRF2: task 1 - Pavel Švarc; 23.3.2018");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                present(g);
            }
        };

        panel.setPreferredSize(new Dimension(width, height));
        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        zb = new ZBufferAlgorithm(img);
        rt = new RasterizerTriangles(zb, cs);
        rl = new RasterizerLines(zb, vertex -> vertex.getColor().mul(1 / vertex.getOne()));
        rendererTriangle = new RendererTriangle(zb,rt);
        rendererLine = new RendererLine(zb,rl);

        //setup matrix
        cam = new Camera()
                .withPosition(new Vec3D(5,3,2))
                .withAzimuth(Math.PI)
                .withZenith(-Math.atan2(2, 5));
        viewMat = cam.getViewMatrix();
        persp =  new Mat4PerspRH(Math.PI / 3,
                (double)height / width, 0.1, 1000);
        orto = new Mat4OrthoRH(7.5, 7.5, 0.1, 1000);
        transl = new Mat4Transl(0.5,0,0);
        translFixed = new Mat4Transl(0.5,0,0);

        //controls
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int currentX = e.getX();
                int currentY = e.getY();
                double dx = previousX-currentX;
                double dy = previousY-currentY;

                //moving camera
                if(SwingUtilities.isLeftMouseButton(e)){
                    cam = cam.addAzimuth(Math.PI * dx / (double)40000);
                    cam = cam.addZenith(Math.PI * dy / (double)40000);
                    viewMat=cam.getViewMatrix();
                    draw();
                    panel.repaint();
                }

                //rotation
                if(SwingUtilities.isRightMouseButton(e)) {
                    identiti = identiti.mul(new Mat4RotZ(Math.PI * dx / (double)9000));
                    identiti = identiti.mul(new Mat4RotY(Math.PI * dy / (double)9000));
                    draw();
                    panel.repaint();
                }
            }
        });

        //zooming (scale)
        panel.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if(e.getWheelRotation() < 0){
                    identiti = identiti.mul(new Mat4Scale(0.75,0.75,0.75));
                }else{
                    identiti = identiti.mul(new Mat4Scale(1.25,1.25,1.25));
                }
                draw();
                panel.repaint();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                previousX=e.getX();
                previousY=e.getY();
            }
        });

        //key bindings
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                //camera - right
                if (e.getKeyCode() == KeyEvent.VK_D) {
                    cam = cam.right(0.1);
                    viewMat = cam.getViewMatrix();
                    draw();
                    panel.repaint();
                }
                //camera - left
                if (e.getKeyCode() == KeyEvent.VK_A) {
                    cam = cam.left(0.1);
                    viewMat = cam.getViewMatrix();
                    draw();
                    panel.repaint();
                }
                //camera - forward
                if (e.getKeyCode() == KeyEvent.VK_W) {
                    cam = cam.forward(0.1);
                    viewMat = cam.getViewMatrix();
                    draw();
                    panel.repaint();
                }
                //camera - backward
                if (e.getKeyCode() == KeyEvent.VK_S) {
                    cam = cam.backward(0.1);
                    viewMat = cam.getViewMatrix();
                    draw();
                    panel.repaint();
                }
                //camera - up
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    cam = cam.up(0.1);
                    viewMat = cam.getViewMatrix();
                    draw();
                    panel.repaint();
                }
                //camera - down
                if (e.getKeyCode() == KeyEvent.VK_F) {
                    cam = cam.down(0.1);
                    viewMat = cam.getViewMatrix();
                    draw();
                    panel.repaint();
                }
                //wireframe translation - down
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    identiti = identiti.mul(new Mat4Transl(0, 0, -0.2));
                    draw();
                    panel.repaint();
                }
                //wireframe translation - up
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    identiti = identiti.mul(new Mat4Transl(0, 0, 0.2));
                    draw();
                    panel.repaint();
                }
                //wireframe translation - left
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    identiti = identiti.mul(new Mat4Transl(0, -0.2, 0));
                    draw();
                    panel.repaint();
                }
                //wireframe translation - right
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    identiti = identiti.mul(new Mat4Transl(0, 0.2, 0));
                    draw();
                    panel.repaint();
                }
                //wireframe translation - forward
                if (e.getKeyCode() == KeyEvent.VK_J) {
                    identiti = identiti.mul(new Mat4Transl(0.2, 0, 0));
                    draw();
                    panel.repaint();
                }
                //wireframe translation - backward
                if (e.getKeyCode() == KeyEvent.VK_U) {
                    identiti = identiti.mul(new Mat4Transl(-0.2, 0, 0));
                    draw();
                    panel.repaint();
                }
                //changing perspective
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    Mat4 tmp;

                    tmp = persp;
                    persp = orto;
                    orto = tmp;

                    draw();
                    panel.repaint();
                }
                //changing view (with help of selectView method below)
                if (e.getKeyCode() == KeyEvent.VK_V) {
                    if (view == 0) view++;
                    view = view % 5;
                    selectView(view);
                    draw();
                    panel.repaint();
                }
                //changing mode (colored/wired)
                if (e.getKeyCode() == KeyEvent.VK_M) {
                    mode = (mode == 0)?1:0;
                    draw();
                    panel.repaint();
                }
                //changing type of flat
                if (e.getKeyCode() == KeyEvent.VK_N) {
                    intTypeFlat++;
                    intTypeFlat = intTypeFlat % 3;
                    f=new Flat(intTypeFlat);
                    draw();
                    panel.repaint();
                }
                //showing objects
                if (e.getKeyCode() == KeyEvent.VK_1) {
                    showPyramid = (showPyramid == 0)?1:0;
                    draw();
                    panel.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_2) {
                    showCube = (showCube == 0)?1:0;
                    draw();
                    panel.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_3) {
                    showFlat = (showFlat == 0)?1:0;
                    draw();
                    panel.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_4) {
                    showAxes = (showAxes == 0)?1:0;
                    draw();
                    panel.repaint();
                }
                //fixed position of cube for testin zb
                if (e.getKeyCode() == KeyEvent.VK_B) {
                    fixedCube = (fixedCube == 0)?1:0;
                    draw();
                    panel.repaint();
                }
            }
        });
    }

    /**
     * method for drawing (rewriting img (BufferedImage))
     */
    public void draw() {
        clear();

        //creating matrix
        Mat4 mObjects = identiti.mul(viewMat).mul(persp).mul(transl);
        Mat4 mFixed = identitiFixed.mul(viewMat).mul(persp).mul(translFixed);

        //axes
        rendererLine.setAxesMode(true);
        if(showAxes==0) img=rendererLine.draw(a.getVertices(),a.getIndices(),mFixed);
        rendererLine.setAxesMode(false);

        //colored
        if(mode==1){
            if(showPyramid==0) img=rendererTriangle.draw(p.getVertices(),p.getIndices(),mObjects);
            if(fixedCube==0) {
                if (showCube == 0) img = rendererTriangle.draw(c.getVertices(), c.getIndices(), mObjects);
            }else{
                if (showCube == 0) img = rendererTriangle.draw(c.getVertices(), c.getIndices(), mFixed);
            }
            if(showFlat==0) img=rendererTriangle.draw(f.getReCountVertices(),f.getIndices(),mObjects);

        }
        //wired
        else {
            if(showPyramid==0) img = rendererLine.draw(p.getVertices(), p.getIndices(),mObjects);
            if(showCube ==0) img = rendererLine.draw(c.getVertices(), c.getIndices(),mObjects);
            if(showFlat==0) img = rendererLine.draw(f.getReCountVertices(), f.getIndices(),mObjects);
        }
    }

    public void start() {
        draw();
        panel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Canvas(800, 600)::start);
    }
    /**
     * method that helps with selecting view
     * @param i integer type of view
     */
    public void selectView(int i){
        if (i == 0 ) {
            viewMat = cam.getViewMatrix();
            view++;
        }else if (i ==1 ) {
            //view from upside (z)
            viewMat = new Mat4ViewRH(	new Vec3D(0.5, 0.5, 5.0),
                    new Vec3D(0.5, 0.5, -5.0),
                    new Vec3D(0.0, 0.0, -1.0));
            view++;
        }else if(i == 2) {
            //view from downside (z)
            viewMat = new Mat4ViewRH(	new Vec3D(0.5, 0.5, -5),
                    new Vec3D(0.5, 0.5, 5.0),
                    new Vec3D(0.0, 0.0, 0.0));
            view++;
        }else if(i == 3) {
            //view from x
            viewMat = new Mat4ViewRH(	new Vec3D(5.0, 0.5, 0.5),
                    new Vec3D(0.0, 0.0, 0.0),
                    new Vec3D(0.0, 0.0, 1.0));
            view++;
        }else if(i == 4) {
            //view from y
            viewMat = new Mat4ViewRH(	new Vec3D(0.5, 5.0, 0.5),
                    new Vec3D(0.0, -1.0, 0.0),
                    new Vec3D(0.0, 0.0, -1.0));
            view++;
        }
    }

    /**
     * method for cleaning zb (image and depth buffer)
     */
    public void clear() {
        zb.clear(new Col(0x2f2f2f));
    }
    public void present(final Graphics graphics) {
        graphics.drawImage(img, 0, 0, null);
    }
}
