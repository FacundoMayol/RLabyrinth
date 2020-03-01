package mainPackage;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainProg {
	
	/*--------*2017*-----------*CREATED BY FACUNDO MAYOL*----------------------*/
	
	static final int GameSquare = 600;
	static final int MegaSize = GameSquare/40;
	static final int MediumSize = GameSquare/70;
	static final int SmallSize = GameSquare/100;
	static final int XLSmallSize = GameSquare/130;
	static final int FinishMilis = 1000;
	static public ArrayList<ArrayList<Cell>> CellList;
	static public boolean MouseOverPlayButton = false;
	static public boolean MouseOverGridOptionButton = false;
	static public boolean MouseOverAlgorithmButton = false;
	static public boolean MouseOverStartMenuButton = false;
	static public boolean MouseOverSpecialButton = false;
	static public Image SpecialImage = null;
	static public String GridOptionString = "Easy";
	static public String Algorithm = "Depth-First Search";
	static public int SizeOfGrid = 0;
	static public int GameState = 0;
	static public String VersionID = "1.0.0.1 - Made by Facundo Mayol *2017*";
	static public Cell CurrentCellInGame;
	static public Cell PreviusCellInGame = null;
	static public Graphics2D PreviusPlayed = null;
	static public JFrame MainFrame = new JFrame("RLaberinth " + VersionID);
	static public MenuBar MainBar = new MenuBar();
	static public Timer AutoCompleteRand = null;
	static public Timer AutoCompleteDFS = null;
	static public Timer AutoCompleteRB = null;
	static public Timer AutoCompleteGBFS = null;
	static public MenuItem SolveRandomlyMenu = new MenuItem("Solve Randomly");
	static public MenuItem DFSMenu = new MenuItem("Solve with Depth-First Search");
	static public MenuItem RBMenu = new MenuItem("Solve with Recursive Backtracker");
	static public MenuItem GBFSMenu = new MenuItem("Solve with GBFS");
	static public JPanel GamePanel = null;
	static public Clip SpecialClip = null;
	static public AudioInputStream SpecialAudioI = null;
	static public boolean OpenSpecial = false;
	static public Coordinate TargetCellCoordinates = null;
	static public ArrayList<Cell> ChainOfActionsGBFS = null; 
	
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	    }
	    catch (ClassNotFoundException e) {
	    }
	    catch (InstantiationException e) {
	    }
	    catch (IllegalAccessException e) {
	    }

		{try {
			
			SpecialClip = AudioSystem.getClip();
			SpecialAudioI = AudioSystem.getAudioInputStream(MainProg.class.getResource("/resources/SpecialMusic.wav"));
			SpecialClip.open(SpecialAudioI);
			FloatControl VolumeAux = (FloatControl) SpecialClip.getControl(FloatControl.Type.MASTER_GAIN);
			VolumeAux.setValue(-10.0f);
			
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {

			e1.printStackTrace();
			ExceptionHandler(e1, "Error playing audio.", "Error saving log file.");
			
		}}

		SpecialImage = new ImageIcon(MainProg.class.getResource("/resources/Special.gif")).getImage().getScaledInstance(300, 300, Image.SCALE_DEFAULT);
		
		MainFrame.setResizable(false);
		MainFrame.setLocationByPlatform(true);
		MainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try{

			MainFrame.setIconImage(new ImageIcon(MainProg.class.getResource("/resources/Icon.png")).getImage());	
			
		}catch(Exception e){
			
			e.printStackTrace();
			ExceptionHandler(e, "Error loading icon for main window.", "Error saving log file.");
			
		}
			
			GamePanel = new JPanel(){
				
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void paintComponent(Graphics g){

					super.paintComponent(g);
					Graphics2D g2D = (Graphics2D) g;
					if(GameState == 0){

						int TitleWidth = getFontMetrics(new Font("serif", Font.PLAIN, 58)).stringWidth("RLaberinth");
						int TitleHeight = getFontMetrics(new Font("serif", Font.PLAIN, 58)).getAscent();
						int PlayWidth = getFontMetrics(new Font("sans serif", Font.PLAIN, 30)).stringWidth("Play");
						int PlayHeight = getFontMetrics(new Font("sans serif", Font.PLAIN, 30)).getAscent();
						int GridWidth = getFontMetrics(new Font("sans serif", Font.PLAIN, 24)).stringWidth("Grid Size :" + GridOptionString);
						int GridHeight = getFontMetrics(new Font("sans serif", Font.PLAIN, 24)).getAscent();
						int AlgorithmWidth = getFontMetrics(new Font("sans serif", Font.PLAIN, 24)).stringWidth(Algorithm);
						int SpecialHeight = getFontMetrics(new Font("sans serif", Font.PLAIN, 8)).getAscent();
						g2D.setPaint(new GradientPaint(0, 0, Color.LIGHT_GRAY, this.getWidth(), this.getHeight(), Color.WHITE));
						g2D.fillRect(0, 0, this.getWidth(), this.getHeight());
						g2D.setPaint(null);
						g2D.setColor(Color.BLACK);
						g2D.setStroke(new BasicStroke(3));
						g2D.drawRect((this.getWidth()/2)-(TitleWidth/2)-10, (this.getHeight()/2)-110-TitleHeight, TitleWidth+17, TitleHeight+10);
						g2D.setFont(new Font("serif", Font.PLAIN, 58));
						g2D.drawString("RLaberinth", (this.getWidth()/2)-(TitleWidth/2), (this.getHeight()/2)-115);
						if(MouseOverPlayButton){

							g2D.setColor(Color.DARK_GRAY.brighter());
							g2D.setFont(new Font("sans serif", Font.BOLD, 30));
							g2D.drawString("Play", (this.getWidth()/2)-(PlayWidth/2), (this.getHeight()/2)+140+(PlayHeight/2));
							
						}else{

							g2D.setColor(Color.BLACK);
							g2D.setFont(new Font("sans serif", Font.BOLD, 30));
							g2D.drawString("Play", (this.getWidth()/2)-(PlayWidth/2), (this.getHeight()/2)+140+(PlayHeight/2));
							
						}
						if(MouseOverGridOptionButton){

							g2D.setColor(Color.DARK_GRAY.brighter());
							g2D.setFont(new Font("sans serif", Font.PLAIN, 24));
							g2D.drawString("Grid Size : " + GridOptionString, (this.getWidth()/2)-((PlayWidth/2)+140)-GridWidth/2, (this.getHeight()/2)+50+(GridHeight/2));
							
						}else{

							g2D.setColor(Color.BLACK);
							g2D.setFont(new Font("sans serif", Font.PLAIN, 24));
							g2D.drawString("Grid Size : " + GridOptionString, (this.getWidth()/2)-((PlayWidth/2)+140)-GridWidth/2, (this.getHeight()/2)+50+(GridHeight/2));
							
						}
						if(MouseOverAlgorithmButton){

							g2D.setColor(Color.DARK_GRAY.brighter());
							g2D.setFont(new Font("sans serif", Font.PLAIN, 24));
							g2D.drawString(Algorithm, (this.getWidth()/2)+((PlayWidth/2)+140)-AlgorithmWidth/2, (this.getHeight()/2)+50+(GridHeight/2));
							
						}else{

							g2D.setColor(Color.BLACK);
							g2D.setFont(new Font("sans serif", Font.PLAIN, 24));
							g2D.drawString(Algorithm, (this.getWidth()/2)+((PlayWidth/2)+140)-AlgorithmWidth/2, (this.getHeight()/2)+50+(GridHeight/2));							
						}
						if(MouseOverSpecialButton){

							g2D.setColor(Color.DARK_GRAY.brighter());
							g2D.setFont(new Font("sans serif", Font.PLAIN, 8));
							g2D.drawString("?", 5, 5+SpecialHeight);
							
						}else{

							g2D.setColor(Color.BLACK);
							g2D.setFont(new Font("sans serif", Font.PLAIN, 8));
							g2D.drawString("?", 5, 5+SpecialHeight);
							
						}
						
					}else if(GameState == 1){

						MainFrame.setMenuBar(null);
						MainFrame.pack();
						int TitleWidth = getFontMetrics(new Font("sans serif", Font.PLAIN, 58)).stringWidth("Loading...");
						g2D.setColor(Color.BLACK);
						g2D.setFont(new Font("sans serif", Font.PLAIN, 58));
						g2D.drawString("Loading...", (this.getWidth()/2)-(TitleWidth/2), (this.getHeight()/2));
						SwingUtilities.invokeLater(new Runnable(){

							@Override
							public void run() {

								Setup(GamePanel);
								
							}
							
						});
						
					}else if(GameState == 2){

						int Size;
						if(SizeOfGrid == 0){
							
							Size = MegaSize;
							
						}else if(SizeOfGrid == 1){
							
							Size = MediumSize;
							
						}else if(SizeOfGrid == 2){
							
							Size = SmallSize;
							
						}else{
							
							Size = XLSmallSize;
							
						}
						g2D.setColor(Color.WHITE);
						g2D.fillRect(0, 0, GameSquare, GameSquare);
						g2D.setStroke(new BasicStroke(Size/5));
						g2D.setColor(Color.BLACK);
						for(int i = 0; i < GameSquare/Size; i++){
							
							for(int j = 0; j < GameSquare/Size; j++){
								
								Cell Temp = CellList.get(i).get(j);
								if(Temp.isStart()){

									g2D.setColor(Color.GREEN.darker().darker());
									g2D.fillRect(Temp.getLocalization().getX()*Size, Temp.getLocalization().getY()*Size,
											Size, Size);
									g2D.setColor(Color.BLACK);
									
								}
								if(Temp.isTarget()){

									g2D.setColor(Color.RED);
									g2D.fillRect(Temp.getLocalization().getX()*Size, Temp.getLocalization().getY()*Size,
											Size, Size);
									g2D.setColor(Color.BLACK);
									
								}
								if(Temp.isVisitedInGame()){
									
									if(Temp.getLocalization().getX() == CurrentCellInGame.getLocalization().getX() &
											Temp.getLocalization().getY() == CurrentCellInGame.getLocalization().getY()){
										
										g2D.setColor(Color.DARK_GRAY);
										g2D.fillRect(Temp.getLocalization().getX()*Size, Temp.getLocalization().getY()*Size,
												Size, Size);
										g2D.setColor(Color.BLACK);
										
									}else{
										
										g2D.setColor(Color.GRAY);
										g2D.fillRect(Temp.getLocalization().getX()*Size, Temp.getLocalization().getY()*Size,
												Size, Size);
										g2D.setColor(Color.BLACK);
										
									}
									
								}
								
							}
							
						}	
						for(int i = 0; i < GameSquare/Size; i++){
							
							for(int j = 0; j < GameSquare/Size; j++){
								
								Cell Temp = CellList.get(i).get(j);
								if(Temp.getBottomWall()){

									g2D.drawLine(Temp.getLocalization().getX()*Size, Temp.getLocalization().getY()*Size+Size,
											Temp.getLocalization().getX()*Size+Size, Temp.getLocalization().getY()*Size+Size);
									
								}
								if(Temp.getTopWall()){

									g2D.drawLine(Temp.getLocalization().getX()*Size, Temp.getLocalization().getY()*Size,
											Temp.getLocalization().getX()*Size+Size, Temp.getLocalization().getY()*Size);
									
								}
								if(Temp.getLeftWall()){

									g2D.drawLine(Temp.getLocalization().getX()*Size, Temp.getLocalization().getY()*Size,
											Temp.getLocalization().getX()*Size, Temp.getLocalization().getY()*Size+Size);
									
								}
								if(Temp.getRightWall()){

									g2D.drawLine(Temp.getLocalization().getX()*Size+Size, Temp.getLocalization().getY()*Size,
											Temp.getLocalization().getX()*Size+Size, Temp.getLocalization().getY()*Size+Size);
									
								}
								
							}
							
						}
						
					}else if(GameState == 3){

						if(AutoCompleteRand.isRunning()){
							
							AutoCompleteRand.stop();
							SolveRandomlyMenu.setLabel("Solve Randomly");
							
						}
						if(AutoCompleteDFS.isRunning()){

							AutoCompleteDFS.stop();
							DFSMenu.setLabel("Solve with Depth-First Search");
							
						}
						if(AutoCompleteRB.isRunning()){

							AutoCompleteRB.stop();
							RBMenu.setLabel("Solve with Recursive Backtracker");
							
						}
						if(AutoCompleteGBFS.isRunning()){

							AutoCompleteGBFS.stop();
							GBFSMenu.setLabel("Solve with GBFS");
							
						}
						g2D.setPaint(new GradientPaint(0, 0, Color.GRAY, this.getWidth(), this.getHeight(), Color.BLACK));
						g2D.fillRect(0, 0, this.getWidth(), this.getHeight());
						g2D.setPaint(null);
						int TitleWidth = getFontMetrics(new Font("sans serif", Font.PLAIN, 58)).stringWidth("YOU WIN");
						int TitleHeight = getFontMetrics(new Font("sans serif", Font.PLAIN, 58)).getAscent();
						int MenuWidth = getFontMetrics(new Font("sans serif", Font.BOLD, 30)).stringWidth("Main Menu");
						int MenuHeight = getFontMetrics(new Font("sans serif", Font.BOLD, 30)).getAscent();
						g2D.setColor(Color.WHITE);
						g2D.setStroke(new BasicStroke(3));
						g2D.drawRect((this.getWidth()/2)-(TitleWidth/2)-10, (this.getHeight()/2)-110-TitleHeight, TitleWidth+17, TitleHeight+10);
						g2D.setFont(new Font("sans serif", Font.PLAIN, 58));
						g2D.drawString("YOU WIN", (this.getWidth()/2)-(TitleWidth/2), (this.getHeight()/2)-115);
						if(MouseOverStartMenuButton){

							g2D.setColor(Color.GRAY);
							g2D.setFont(new Font("sans serif", Font.BOLD, 30));
							g2D.drawString("Main Menu", (this.getWidth()/2)-(MenuWidth/2), (this.getHeight()/2)+140+(MenuHeight/2));
							
						}else{

							g2D.setColor(Color.WHITE);
							g2D.setFont(new Font("sans serif", Font.BOLD, 30));
							g2D.drawString("Main Menu", (this.getWidth()/2)-(MenuWidth/2), (this.getHeight()/2)+140+(MenuHeight/2));
							
						}
						
					}
					
				};
				
			};
			GamePanel.addMouseListener(new MouseAdapter(){

				@Override
				public void mouseClicked(MouseEvent ME) {
				
					if(GameState == 0){

						if(MouseOverPlayButton){
							
							GameState = 1;
							GamePanel.repaint();
							
						}
						if(MouseOverAlgorithmButton){
							
							if(Algorithm.equals("Depth-First Search")){
								
								Algorithm = "Kruskal";
								
							}else if(Algorithm.equals("Kruskal")){
								
								Algorithm = "Aldous Broder";
								
							}else if(Algorithm.equals("Aldous Broder")){
								
								Algorithm = "Growing Tree";
								
							}else if(Algorithm.equals("Growing Tree")){
								
								Algorithm = "Binary Tree";
								
							}else if(Algorithm.equals("Binary Tree")){
								
								Algorithm = "Modified Prim";
								
							}else if(Algorithm.equals("Modified Prim")){
								
								Algorithm = "Depth-First Search";
								
							}
							GamePanel.repaint();
							
						}
						if(MouseOverSpecialButton){

							if(!OpenSpecial){
								
								OpenSpecial = true;
								JLabel SpecialLabel = new JLabel(new ImageIcon(SpecialImage));
								JPanel SpecialMain = new JPanel();
								JPanel SpecialText = new JPanel();
								SpecialMain.setLayout(new BorderLayout());
								SpecialMain.setBackground(Color.GRAY);
								SpecialText.setPreferredSize(new Dimension(0, 25));
								JLabel Creator = new JLabel("Created by Facundo Mayol. Use arrows keys to play");
								Creator.setHorizontalAlignment(SwingConstants.CENTER);
								Creator.setVerticalAlignment(SwingConstants.CENTER);
								SpecialText.add(Creator);
								JFrame SpecialFrame = new JFrame("...");
								SpecialFrame.setLayout(new BorderLayout());
								SpecialFrame.setUndecorated(false);
								SpecialFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
								SpecialFrame.addWindowListener(new WindowAdapter(){

									@Override
									public void windowClosing(WindowEvent WE) {

										SpecialFrame.dispose();
										OpenSpecial = false;
										try {

											SpecialClip.close();
											SpecialAudioI.close();
											SpecialClip = AudioSystem.getClip();
											SpecialAudioI = AudioSystem.getAudioInputStream(MainProg.class.getResource("/resources/SpecialMusic.wav"));
											SpecialClip.open(SpecialAudioI);
											FloatControl VolumeAux = (FloatControl) SpecialClip.getControl(FloatControl.Type.MASTER_GAIN);
											VolumeAux.setValue(-10.0f);
											
										} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {

											e.printStackTrace();
											ExceptionHandler(e, "Error restarting audio.", "Error saving log file.");
											
										}
										
									}
									
								});
								SpecialFrame.setResizable(false);
								SpecialFrame.setIconImage(new ImageIcon(MainProg.class.getResource("/resources/Icon.png")).getImage());
								SpecialFrame.getContentPane().removeAll();
								SpecialMain.add(SpecialLabel, BorderLayout.CENTER);
								SpecialFrame.getContentPane().add(SpecialMain, BorderLayout.CENTER);
								SpecialFrame.getContentPane().add(SpecialText, BorderLayout.SOUTH);
								SpecialFrame.pack();
								SpecialFrame.setLocationRelativeTo(MainFrame);
								SpecialClip.loop(Clip.LOOP_CONTINUOUSLY);
								SpecialFrame.setVisible(true);	
								
							}
							
						}
						if(MouseOverGridOptionButton){

							SizeOfGrid++;
							if(SizeOfGrid == 4){
								
								GridOptionString = "Easy";
								SizeOfGrid = 0;
								
							}else if(SizeOfGrid == 3){

								GridOptionString = "Mega Epic";
								
							}else if(SizeOfGrid == 2){

								GridOptionString = "Hard";
								
							}else if(SizeOfGrid == 1){

								GridOptionString = "Medium";
								
							}
							GamePanel.repaint();
							
						}		
						
					}else if(GameState == 3){
						
						if(MouseOverStartMenuButton){
							
							GameState = 0;
							GamePanel.repaint();
							
						}
						
					}
					
				}
				
			});
			GamePanel.addMouseMotionListener(new MouseMotionAdapter(){

				@Override
				public void mouseMoved(MouseEvent ME) {
					
					if(GameState == 0){

						int PlayWidth = GamePanel.getFontMetrics(new Font("sans serif", Font.PLAIN, 30)).stringWidth("Play");
						int PlayHeight = GamePanel.getFontMetrics(new Font("sans serif", Font.PLAIN, 30)).getAscent();
						if(new Rectangle((GamePanel.getWidth()/2)-(PlayWidth/2), (GamePanel.getHeight()/2)+140-(PlayHeight/2), PlayWidth, PlayHeight).contains(ME.getPoint())){
							
							MouseOverPlayButton = true;
							GamePanel.repaint();
							
						}else{
							
							MouseOverPlayButton = false;
							GamePanel.repaint();
							
						}
						int GridWidth = GamePanel.getFontMetrics(new Font("sans serif", Font.PLAIN, 24)).stringWidth("Grid Size : " + GridOptionString);
						int GridHeight = GamePanel.getFontMetrics(new Font("sans serif", Font.PLAIN, 24)).getAscent();
						if(new Rectangle((GamePanel.getWidth()/2)-((PlayWidth/2)+140)-GridWidth/2, (GamePanel.getHeight()/2)+50-(GridHeight/2), GridWidth, GridHeight).contains(ME.getPoint())){
							
							MouseOverGridOptionButton = true;
							GamePanel.repaint();
							
						}else{
							
							MouseOverGridOptionButton = false;
							GamePanel.repaint();
							
						}
						int AlgorithmWidth = GamePanel.getFontMetrics(new Font("sans serif", Font.PLAIN, 24)).stringWidth(Algorithm);
						if(new Rectangle((GamePanel.getWidth()/2)+((PlayWidth/2)+140)-AlgorithmWidth/2, (GamePanel.getHeight()/2)+50, AlgorithmWidth, GridHeight).contains(ME.getPoint())){

							MouseOverAlgorithmButton = true;
							GamePanel.repaint();
							
						}else{
							
							MouseOverAlgorithmButton = false;
							GamePanel.repaint();
							
						}
						int SpecialWidth = GamePanel.getFontMetrics(new Font("sans serif", Font.PLAIN, 24)).stringWidth("?");
						int SpecialHeight = GamePanel.getFontMetrics(new Font("sans serif", Font.PLAIN, 24)).getAscent();
						if(new Rectangle(5, 5, SpecialWidth, SpecialHeight).contains(ME.getPoint())){

							MouseOverSpecialButton = true;
							GamePanel.repaint();
							
						}else{
							
							MouseOverSpecialButton = false;
							GamePanel.repaint();
							
						}
						
					}else if(GameState == 3){
						
						int MenuWidth = GamePanel.getFontMetrics(new Font("sans serif", Font.PLAIN, 30)).stringWidth("Main Menu");
						int MenuHeight = GamePanel.getFontMetrics(new Font("sans serif", Font.PLAIN, 30)).getAscent();
						if(new Rectangle((GamePanel.getWidth()/2)-(MenuWidth/2), (GamePanel.getHeight()/2)+140-(MenuHeight/2), MenuWidth, MenuHeight).contains(ME.getPoint())){
							
							MouseOverStartMenuButton = true;
							GamePanel.repaint();
							
						}else{
							
							MouseOverStartMenuButton = false;
							GamePanel.repaint();
							
						}
						
					}
					
				}
				
			});

			Timer ControlsUp = new Timer(70, new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {

					if(GameState != 2){
						
						return;
						
					}
					if(AutoCompleteRand.isRunning() | AutoCompleteDFS.isRunning() | AutoCompleteRB.isRunning() | AutoCompleteGBFS.isRunning()){
						
						return;
						
					}
					int TempX = CurrentCellInGame.getLocalization().getX(), TempY = CurrentCellInGame.getLocalization().getY();
					if(TempY != 0){

						boolean NoWalls = true;
						
						if(CurrentCellInGame.getTopWall()){
							
							NoWalls = false;
							
						}else if(CellList.get(TempY-1).get(TempX).getBottomWall()){
							
							NoWalls = false;
							
						}
						
						if(NoWalls){
							
							if(CellList.get(TempY-1).get(TempX).isTarget()){
								
								GameState++;
								CellList = null;
								TargetCellCoordinates = null;
								ChainOfActionsGBFS = null;
								Timer FinishTime = new Timer(MainProg.FinishMilis, new ActionListener(){
									
									public void actionPerformed(ActionEvent e) {

										MainFrame.setMenuBar(null);
										MainFrame.pack();
										GamePanel.repaint();
										
									}
									
								});
								FinishTime.setRepeats(false);
								FinishTime.start();
								
							}else{
								
								PreviusCellInGame = CurrentCellInGame; 
								CurrentCellInGame = CellList.get(TempY-1).get(TempX);
								CellList.get(TempY-1).get(TempX).setVisitedInGame(true);
								GamePanel.repaint();	
								
							}
							
						}
						
					}
					
				}
				
			});
			Timer ControlsRight = new Timer(70, new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {

					if(GameState != 2){
						
						return;
						
					}
					if(AutoCompleteRand.isRunning() | AutoCompleteDFS.isRunning() | AutoCompleteRB.isRunning() | AutoCompleteGBFS.isRunning()){
						
						return;
						
					}
					int TempX = CurrentCellInGame.getLocalization().getX(), TempY = CurrentCellInGame.getLocalization().getY();
					if(TempX != GameSquare/CurrentCellInGame.getSize()-1){
						
						boolean NoWalls = true;
						
						if(CurrentCellInGame.getRightWall()){
							
							NoWalls = false;
							
						}else if(CellList.get(TempY).get(TempX+1).getLeftWall()){
							

							NoWalls = false;
							
						}
						
						if(NoWalls){

							if(CellList.get(TempY).get(TempX+1).isTarget()){

								GameState++;
								CellList = null;
								TargetCellCoordinates = null;
								ChainOfActionsGBFS = null;
								Timer FinishTime = new Timer(MainProg.FinishMilis, new ActionListener(){
									
									public void actionPerformed(ActionEvent e) {

										MainFrame.setMenuBar(null);
										MainFrame.pack();
										GamePanel.repaint();
										
									}
									
								});
								FinishTime.setRepeats(false);
								FinishTime.start();
								
							}else{

								PreviusCellInGame = CurrentCellInGame; 
								CurrentCellInGame = CellList.get(TempY).get(TempX+1);
								CellList.get(TempY).get(TempX+1).setVisitedInGame(true);
								GamePanel.repaint();	
								
							}
							
						}
						
					}
					
				}
				
			});
			Timer ControlsDown = new Timer(70, new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {

					if(GameState != 2){
						
						return;
						
					}
					if(AutoCompleteRand.isRunning() | AutoCompleteDFS.isRunning() | AutoCompleteRB.isRunning() | AutoCompleteGBFS.isRunning()){
						
						return;
						
					}
					int TempX = CurrentCellInGame.getLocalization().getX(), TempY = CurrentCellInGame.getLocalization().getY();
					if(TempY != GameSquare/CurrentCellInGame.getSize()-1){

						boolean NoWalls = true;
						
						if(CurrentCellInGame.getBottomWall()){

							NoWalls = false;
							
						}else if(CellList.get(TempY+1).get(TempX).getTopWall()){

							NoWalls = false;
							
						}

						if(NoWalls){
							
							if(CellList.get(TempY+1).get(TempX).isTarget()){

								GameState++;
								CellList = null;
								TargetCellCoordinates = null;
								ChainOfActionsGBFS = null;
								Timer FinishTime = new Timer(MainProg.FinishMilis, new ActionListener(){
									
									public void actionPerformed(ActionEvent e) {

										MainFrame.setMenuBar(null);
										MainFrame.pack();
										GamePanel.repaint();
										
									}
									
								});
								FinishTime.setRepeats(false);
								FinishTime.start();
								
							}else{

								PreviusCellInGame = CurrentCellInGame; 
								CurrentCellInGame = CellList.get(TempY+1).get(TempX);
								CellList.get(TempY+1).get(TempX).setVisitedInGame(true);
								GamePanel.repaint();	
								
							}
							
						}
						
					}
					
				}
				
			});
			Timer ControlsLeft = new Timer(70, new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {

					if(GameState != 2){
						
						return;
						
					}
					if(AutoCompleteRand.isRunning() | AutoCompleteDFS.isRunning() | AutoCompleteRB.isRunning() | AutoCompleteGBFS.isRunning()){
						
						return;
						
					}
					int TempX = CurrentCellInGame.getLocalization().getX(), TempY = CurrentCellInGame.getLocalization().getY();
					if(TempX != 0){

						boolean NoWalls = true;
						
						if(CurrentCellInGame.getLeftWall()){
							
							NoWalls = false;
							
						}else if(CellList.get(TempY).get(TempX-1).getRightWall()){
							
							NoWalls = false;
							
						}
						
						if(NoWalls){
							
							if(CellList.get(TempY).get(TempX-1).isTarget()){

								GameState++;
								CellList = null;
								TargetCellCoordinates = null;
								ChainOfActionsGBFS = null;
								Timer FinishTime = new Timer(MainProg.FinishMilis, new ActionListener(){
									
									public void actionPerformed(ActionEvent e) {

										MainFrame.setMenuBar(null);
										MainFrame.pack();
										GamePanel.repaint();
										
									}
									
								});
								FinishTime.setRepeats(false);
								FinishTime.start();
								
							}else{

								PreviusCellInGame = CurrentCellInGame; 
								CurrentCellInGame = CellList.get(TempY).get(TempX-1);
								CellList.get(TempY).get(TempX-1).setVisitedInGame(true);
								GamePanel.repaint();	
								
							}
							
						}
						
					}
					
				}
				
			});
			GamePanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "StartActionUp");
			GamePanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "StartActionDown");
			GamePanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "StartActionLeft");
			GamePanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "StartActionRight");
			GamePanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "StartActionUpRelased");
			GamePanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "StartActionDownRelased");
			GamePanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "StartActionLeftRelased");
			GamePanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "StartActionRightRelased");
			GamePanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0, false), "GoMainMenu");
			GamePanel.getActionMap().put("StartActionUp", new AbstractAction(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					
					ControlsUp.start();
					
				}
				
			});
			GamePanel.getActionMap().put("StartActionUpRelased", new AbstractAction(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					
					ControlsUp.stop();
					
				}
				
			});
			GamePanel.getActionMap().put("StartActionDown", new AbstractAction(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					
					ControlsDown.start();
					
				}
				
			});
			GamePanel.getActionMap().put("StartActionDownRelased", new AbstractAction(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					
					ControlsDown.stop();
					
				}
				
			});
			GamePanel.getActionMap().put("StartActionLeft", new AbstractAction(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					
					ControlsLeft.start();
					
				}
				
			});
			GamePanel.getActionMap().put("StartActionLeftRelased", new AbstractAction(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					
					ControlsLeft.stop();
					
				}
				
			});
			GamePanel.getActionMap().put("StartActionRight", new AbstractAction(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					
					ControlsRight.start();
					
				}
				
			});
			GamePanel.getActionMap().put("StartActionRightRelased", new AbstractAction(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					
					ControlsRight.stop();
					
				}
				
			});
			GamePanel.getActionMap().put("GoMainMenu", new AbstractAction(){

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					
					if(GameState != 2){
						
						return;
						
					}
					if(AutoCompleteRand.isRunning()){
						
						AutoCompleteRand.stop();
						SolveRandomlyMenu.setLabel("Solve Randomly");
						
					}
					if(AutoCompleteDFS.isRunning()){

						AutoCompleteDFS.stop();
						DFSMenu.setLabel("Solve with Depth-First Search");
						
					}
					if(AutoCompleteRB.isRunning()){

						AutoCompleteRB.stop();
						RBMenu.setLabel("Solve with Recursive Backtracker");
						
					}
					if(AutoCompleteGBFS.isRunning()){

						AutoCompleteGBFS.stop();
						GBFSMenu.setLabel("Solve with GBFS");
						
					}
					GameState = 0;
					MainFrame.setMenuBar(null);
					MainFrame.pack();
					GamePanel.repaint();
					
				}
				
			});
			
			GamePanel.setPreferredSize(new Dimension(GameSquare, GameSquare));
			GamePanel.setMinimumSize(new Dimension(GameSquare, GameSquare));
			GamePanel.setMaximumSize(new Dimension(GameSquare, GameSquare));
			GamePanel.setOpaque(true);
			MainFrame.setContentPane(GamePanel);

				SolveRandomlyMenu.addActionListener(new ActionListener(){
		
						@Override
						public void actionPerformed(ActionEvent AE) {

							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							
							if(AutoCompleteRand.isRunning() == false){
		
								AutoCompleteRand.start();
								SolveRandomlyMenu.setLabel("Stop Solving");
								
							}else{
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							
						}
						
					});
				DFSMenu.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent AE) {
						
						if(AutoCompleteRand.isRunning()){
							
							AutoCompleteRand.stop();
							SolveRandomlyMenu.setLabel("Solve Randomly");
							
						}
						if(AutoCompleteRB.isRunning()){

							AutoCompleteRB.stop();
							RBMenu.setLabel("Solve with Recursive Backtracker");
							
						}
						if(AutoCompleteGBFS.isRunning()){

							AutoCompleteGBFS.stop();
							GBFSMenu.setLabel("Solve with GBFS");
							
						}
						
						if(AutoCompleteDFS.isRunning() == false){
	
							AutoCompleteDFS.start();
							DFSMenu.setLabel("Stop Solving");
							
						}else{
							
							AutoCompleteDFS.stop();
							DFSMenu.setLabel("Solve with Depth-First Search");
							
						}
						
					}
					
				});
				RBMenu.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent AE) {
						
						if(AutoCompleteRand.isRunning()){
							
							AutoCompleteRand.stop();
							SolveRandomlyMenu.setLabel("Solve Randomly");
							
						}
						if(AutoCompleteDFS.isRunning()){

							AutoCompleteDFS.stop();
							DFSMenu.setLabel("Solve with Depth-First Search");
							
						}
						if(AutoCompleteGBFS.isRunning()){

							AutoCompleteGBFS.stop();
							GBFSMenu.setLabel("Solve with RBFS");
							
						}
						
						if(AutoCompleteRB.isRunning() == false){
	
							AutoCompleteRB.start();
							RBMenu.setLabel("Stop Solving");
							
						}else{
							
							AutoCompleteRB.stop();
							RBMenu.setLabel("Solve with Recursive Backtracker");
							
						}
						
					}
					
				});
				GBFSMenu.addActionListener(new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent AE) {
						
						if(AutoCompleteRand.isRunning()){
							
							AutoCompleteRand.stop();
							SolveRandomlyMenu.setLabel("Solve Randomly");
							
						}
						if(AutoCompleteDFS.isRunning()){

							AutoCompleteDFS.stop();
							DFSMenu.setLabel("Solve with Depth-First Search");
							
						}
						if(AutoCompleteRB.isRunning()){

							AutoCompleteRB.stop();
							RBMenu.setLabel("Solve with Recursive Backtracker");
							
						}
						
						if(AutoCompleteGBFS.isRunning() == false){
							
							GBFS();
							AutoCompleteGBFS.start();
							GBFSMenu.setLabel("Stop Solving");
							
						}else{
							
							AutoCompleteGBFS.stop();
							GBFSMenu.setLabel("Solve with GBFS");
							
						}
						
					}
					
				});
				AutoCompleteRand = new Timer(3, new AbstractAction(){
		
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;
		
						@Override
						public void actionPerformed(ActionEvent AE) {
		
							if(GameState != 2){
								
								return;
								
							}
							ArrayList<Cell> PosiblesRoutes = new ArrayList<Cell>(4);
							Cell NextTemp = null;
							int TempX = CurrentCellInGame.getLocalization().getX(), TempY = CurrentCellInGame.getLocalization().getY();
							if(TempX != 0){
								
								if(!CurrentCellInGame.getLeftWall()){
									
									if(PreviusCellInGame != null){

										if(!(TempY == PreviusCellInGame.getLocalization().getY() &
												TempX-1 == PreviusCellInGame.getLocalization().getX())){

											PosiblesRoutes.add(CellList.get(TempY).get(TempX-1));	
											
										}	
										
									}else{

										PosiblesRoutes.add(CellList.get(TempY).get(TempX-1));
										
									}
									
								}
								
							}
							if(TempY != GameSquare/CurrentCellInGame.getSize()-1){
		
								if(!CurrentCellInGame.getBottomWall()){
									
									if(PreviusCellInGame != null){

										if(!(TempY+1 == PreviusCellInGame.getLocalization().getY() &
												TempX == PreviusCellInGame.getLocalization().getX())){

											PosiblesRoutes.add(CellList.get(TempY+1).get(TempX));	
											
										}	
										
									}else{

										PosiblesRoutes.add(CellList.get(TempY+1).get(TempX));
										
									}
									
								}
								
							}
							if(TempX != GameSquare/CurrentCellInGame.getSize()-1){
		
								if(!CurrentCellInGame.getRightWall()){
									
									if(PreviusCellInGame != null){

										if(!(TempY == PreviusCellInGame.getLocalization().getY() &
												TempX+1 == PreviusCellInGame.getLocalization().getX())){

											PosiblesRoutes.add(CellList.get(TempY).get(TempX+1));	
											
										}	
										
									}else{
										
										PosiblesRoutes.add(CellList.get(TempY).get(TempX+1));
										
									}
									
								}
								
							}
							if(TempY != 0){
		
								if(!CurrentCellInGame.getTopWall()){
		
									if(PreviusCellInGame != null){

										if(!(TempY-1 == PreviusCellInGame.getLocalization().getY() &
												TempX == PreviusCellInGame.getLocalization().getX())){

											PosiblesRoutes.add(CellList.get(TempY-1).get(TempX));	
											
										}	
										
									}else{

										PosiblesRoutes.add(CellList.get(TempY-1).get(TempX));
										
									}
									
								}
								
							}
							if(PosiblesRoutes.size() >= 1){
								 
								NextTemp = PosiblesRoutes.get((PosiblesRoutes.size() != 1)?Rand(0, PosiblesRoutes.size()):0);	
								
							}else{
								
								NextTemp = PreviusCellInGame;
								
							}
							if(NextTemp.isTarget()){

								GameState++;
								CellList = null;
								TargetCellCoordinates = null;
								ChainOfActionsGBFS = null;
								Timer FinishTime = new Timer(MainProg.FinishMilis, new ActionListener(){
									
									public void actionPerformed(ActionEvent e) {
		
										MainFrame.setMenuBar(null);
										MainFrame.pack();
										GamePanel.repaint();
										
									}
									
								});
								FinishTime.setRepeats(false);
								FinishTime.start();
								
							}else{
		
								PreviusCellInGame = CurrentCellInGame;
								CurrentCellInGame = NextTemp;
								CellList.get(NextTemp.getLocalization().getY()).get(NextTemp.getLocalization().getX()).setVisitedInGame(true);
								GamePanel.repaint();	
								
							}
							
						}
						
					});
				AutoCompleteDFS = new Timer(3, new AbstractAction(){
		
						/**
						 * 
						 */
						private static final long serialVersionUID = 1L;
		
						@Override
						public void actionPerformed(ActionEvent AE) {
		
							if(GameState != 2){
								
								return;
								
							}
							ArrayList<Cell> PosiblesRoutes = new ArrayList<Cell>(4);
							Cell NextTemp = null;
							int TempX = CurrentCellInGame.getLocalization().getX(), TempY = CurrentCellInGame.getLocalization().getY();
							if(TempX != 0){
								
								if(!CurrentCellInGame.getLeftWall()){
									
									if(!CellList.get(TempY).get(TempX-1).isVisitedInGame()){
										
										PosiblesRoutes.add(CellList.get(TempY).get(TempX-1));
										
									}
									
								}
								
							}
							if(TempY != GameSquare/CurrentCellInGame.getSize()-1){
		
								if(!CurrentCellInGame.getBottomWall()){
									
									if(!CellList.get(TempY+1).get(TempX).isVisitedInGame()){

										PosiblesRoutes.add(CellList.get(TempY+1).get(TempX));
										
									}
									
								}
								
							}
							if(TempX != GameSquare/CurrentCellInGame.getSize()-1){
								
								if(!CurrentCellInGame.getRightWall()){
									
									if(!CellList.get(TempY).get(TempX+1).isVisitedInGame()){
										
										PosiblesRoutes.add(CellList.get(TempY).get(TempX+1));
										
									}
									
								}
								
							}
							if(TempY != 0){
		
								if(!CurrentCellInGame.getTopWall()){
		
									if(!CellList.get(TempY-1).get(TempX).isVisitedInGame()){

										PosiblesRoutes.add(CellList.get(TempY-1).get(TempX));
										
									}
									
								}
								
							}
							if(PosiblesRoutes.size() >= 1){
								
								NextTemp = PosiblesRoutes.get((PosiblesRoutes.size() > 1)?Rand(0, PosiblesRoutes.size()):0);	
								
							}else{

								Cell TempCellNext = null;
								while(true){

									do{
										
										TempX = Rand(0, GameSquare/CurrentCellInGame.getSize());
										TempY = Rand(0, GameSquare/CurrentCellInGame.getSize());
										
									}while(!CellList.get(TempY).get(TempX).isVisitedInGame());
										
									if(TempX != 0){
										
										if(!CellList.get(TempY).get(TempX).getLeftWall()){
											
											if(!CellList.get(TempY).get(TempX-1).isVisitedInGame()){
												
												TempCellNext = CellList.get(TempY).get(TempX);
												break;
												
											}
											
										}
										
									}
									if(TempY != GameSquare/CurrentCellInGame.getSize()-1){
				
										if(!CellList.get(TempY).get(TempX).getBottomWall()){
											
											if(!CellList.get(TempY+1).get(TempX).isVisitedInGame()){

												TempCellNext = CellList.get(TempY).get(TempX);
												break;
												
											}
											
										}
										
									}
									if(TempX != GameSquare/CurrentCellInGame.getSize()-1){
										
										if(!CellList.get(TempY).get(TempX).getRightWall()){
											
											if(!CellList.get(TempY).get(TempX+1).isVisitedInGame()){

												TempCellNext = CellList.get(TempY).get(TempX);
												break;
												
											}
											
										}
										
									}
									if(TempY != 0){
				
										if(!CellList.get(TempY).get(TempX).getTopWall()){
				
											if(!CellList.get(TempY-1).get(TempX).isVisitedInGame()){

												TempCellNext = CellList.get(TempY).get(TempX);
												break;
												
											}
											
										}
										
									}
									
								}
								NextTemp = TempCellNext;
								
							}
							if(NextTemp.isTarget()){

								GameState++;
								CellList = null;
								TargetCellCoordinates = null;
								ChainOfActionsGBFS = null;
								Timer FinishTime = new Timer(MainProg.FinishMilis, new ActionListener(){
									
									public void actionPerformed(ActionEvent e) {
		
										MainFrame.setMenuBar(null);
										MainFrame.pack();
										GamePanel.repaint();
										
									}
									
								});
								FinishTime.setRepeats(false);
								FinishTime.start();
								
							}else{
		
								PreviusCellInGame = CurrentCellInGame;
								CurrentCellInGame = NextTemp;
								CellList.get(NextTemp.getLocalization().getY()).get(NextTemp.getLocalization().getX()).setVisitedInGame(true);
								GamePanel.repaint();	
								
							}
							
						}
						
					});
				AutoCompleteGBFS = new Timer(30, new AbstractAction(){
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
	
					@Override
					public void actionPerformed(ActionEvent AE) {
	
						if(GameState != 2){
							
							return;
							
						}
						Cell NextTemp;
						if(ChainOfActionsGBFS.size()>0){
							
							NextTemp = ChainOfActionsGBFS.get(0);
							ChainOfActionsGBFS.remove(0);
							
						}else{
							
							NextTemp = CellList.get(TargetCellCoordinates.getY()).get(TargetCellCoordinates.getX());
							
						}
						if(NextTemp.isTarget()){

							GameState++;
							CellList = null;
							TargetCellCoordinates = null;
							ChainOfActionsGBFS = null;
							Timer FinishTime = new Timer(MainProg.FinishMilis, new ActionListener(){
								
								public void actionPerformed(ActionEvent e) {
	
									MainFrame.setMenuBar(null);
									MainFrame.pack();
									GamePanel.repaint();
									
								}
								
							});
							FinishTime.setRepeats(false);
							FinishTime.start();
							
						}else{
	
							PreviusCellInGame = CurrentCellInGame;
							CurrentCellInGame = NextTemp;
							CellList.get(NextTemp.getLocalization().getY()).get(NextTemp.getLocalization().getX()).setVisitedInGame(true);
							GamePanel.repaint();	
							
						}
						
					}
					
				});
				AutoCompleteRB = new Timer(3, new AbstractAction(){
					
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
	
					@Override
					public void actionPerformed(ActionEvent AE) {
	
						if(GameState != 2){
							
							return;
							
						}
						ArrayList<Cell> PosiblesRoutes = new ArrayList<Cell>(4);
						Cell NextTemp = null;
						int TempX = CurrentCellInGame.getLocalization().getX(), TempY = CurrentCellInGame.getLocalization().getY();
						if(TempX != 0){
							
							if(!CurrentCellInGame.getLeftWall()){
								
								if(PreviusCellInGame != null){

									if(!(TempY == PreviusCellInGame.getLocalization().getY() &
											TempX-1 == PreviusCellInGame.getLocalization().getX())){

										PosiblesRoutes.add(CellList.get(TempY).get(TempX-1));	
										
									}	
									
								}else{

									PosiblesRoutes.add(CellList.get(TempY).get(TempX-1));
									
								}
								
							}
							
						}
						if(TempY != GameSquare/CurrentCellInGame.getSize()-1){
	
							if(!CurrentCellInGame.getBottomWall()){
								
								if(PreviusCellInGame != null){

									if(!(TempY+1 == PreviusCellInGame.getLocalization().getY() &
											TempX == PreviusCellInGame.getLocalization().getX())){

										PosiblesRoutes.add(CellList.get(TempY+1).get(TempX));	
										
									}	
									
								}else{

									PosiblesRoutes.add(CellList.get(TempY+1).get(TempX));
									
								}
								
							}
							
						}
						if(TempX != GameSquare/CurrentCellInGame.getSize()-1){
	
							if(!CurrentCellInGame.getRightWall()){
								
								if(PreviusCellInGame != null){

									if(!(TempY == PreviusCellInGame.getLocalization().getY() &
											TempX+1 == PreviusCellInGame.getLocalization().getX())){

										PosiblesRoutes.add(CellList.get(TempY).get(TempX+1));	
										
									}	
									
								}else{
									
									PosiblesRoutes.add(CellList.get(TempY).get(TempX+1));
									
								}
								
							}
							
						}
						if(TempY != 0){
	
							if(!CurrentCellInGame.getTopWall()){
	
								if(PreviusCellInGame != null){

									if(!(TempY-1 == PreviusCellInGame.getLocalization().getY() &
											TempX == PreviusCellInGame.getLocalization().getX())){

										PosiblesRoutes.add(CellList.get(TempY-1).get(TempX));	
										
									}	
									
								}else{

									PosiblesRoutes.add(CellList.get(TempY-1).get(TempX));
									
								}
								
							}
							
						}
						if(PosiblesRoutes.size() > 1){
							
							boolean HasUnvisited = false;
							int i = 0;
							while(i < PosiblesRoutes.size()){
								
								if(!PosiblesRoutes.get(i).isVisitedInGame()){
									
									HasUnvisited = true;
									break;
									
								}
								i++;
								
							}
							if(HasUnvisited){
								
								i = 0;
								while(i < PosiblesRoutes.size()){
									
									if(PosiblesRoutes.get(i).isVisitedInGame()){
										
										PosiblesRoutes.remove(i);
										
									}else{
										
										i++;
										
									}
									
								}
								NextTemp = PosiblesRoutes.get((PosiblesRoutes.size() > 1)?Rand(0, PosiblesRoutes.size()):0);
								
							}else{

								NextTemp = PosiblesRoutes.get(Rand(0, PosiblesRoutes.size()));
								
							}	
							
						}else if(PosiblesRoutes.size() == 1){

							NextTemp = PosiblesRoutes.get(0);
							
						}else{

							NextTemp = PreviusCellInGame;
							
						}
						if(NextTemp.isTarget()){

							GameState++;
							CellList = null;
							TargetCellCoordinates = null;
							ChainOfActionsGBFS = null;
							Timer FinishTime = new Timer(MainProg.FinishMilis, new ActionListener(){
								
								public void actionPerformed(ActionEvent e) {
	
									MainFrame.setMenuBar(null);
									MainFrame.pack();
									GamePanel.repaint();
									
								}
								
							});
							FinishTime.setRepeats(false);
							FinishTime.start();
							
						}else{
	
							PreviusCellInGame = CurrentCellInGame;
							CurrentCellInGame = NextTemp;
							CellList.get(NextTemp.getLocalization().getY()).get(NextTemp.getLocalization().getX()).setVisitedInGame(true);
							GamePanel.repaint();	
							
						}
						
					}
					
				});
				Menu DiffMenu = new Menu("Difficulty");
					MenuItem Easy = new MenuItem("Easy");
					Easy.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(SizeOfGrid == 0){
								
								return;
								
							}
							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							GridOptionString = "Easy";
							SizeOfGrid = 0;
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
					MenuItem Medium = new MenuItem("Medium");
					Medium.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(SizeOfGrid == 1){
								
								return;
								
							}
							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							GridOptionString = "Medium";
							SizeOfGrid = 1;
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
					MenuItem Hard = new MenuItem("Hard");
					Hard.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(SizeOfGrid == 2){
								
								return;
								
							}
							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with RBFS");
								
							}
							GridOptionString = "Hard";
							SizeOfGrid = 2;
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
					MenuItem MegaEpic = new MenuItem("Mega Epic");
					MegaEpic.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(SizeOfGrid == 3){
								
								return;
								
							}
							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							GridOptionString = "Mega Epic";
							SizeOfGrid = 3;
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
				DiffMenu.add(Easy);
				DiffMenu.add(Medium);
				DiffMenu.add(Hard);
				DiffMenu.add(MegaEpic);
				Menu AlgorithmMenu = new Menu("Algorithm");
					MenuItem DepthFirstSearchAlgorithm = new MenuItem("Depth-First Search");
					DepthFirstSearchAlgorithm.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							Algorithm = "Depth-First Search";
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
					MenuItem KruskalAlgorithm = new MenuItem("Kruskal");
					KruskalAlgorithm.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							Algorithm = "Kruskal";
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
					MenuItem BinaryTAlgorithm = new MenuItem("Binary Tree");
					BinaryTAlgorithm.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							Algorithm = "Binary Tree";
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
					MenuItem GrowTAlgorithm = new MenuItem("Growing Tree");
					GrowTAlgorithm.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							Algorithm = "Growing Tree";
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
					MenuItem ModPAlgorithm = new MenuItem("Modified Prim");
					ModPAlgorithm.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							Algorithm = "Modified Prim";
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
					MenuItem ABAlgorithm = new MenuItem("Aldous Broder");
					ABAlgorithm.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							Algorithm = "Aldous Broder";
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
				AlgorithmMenu.add(DepthFirstSearchAlgorithm);
				AlgorithmMenu.add(KruskalAlgorithm);
				AlgorithmMenu.add(BinaryTAlgorithm);
				AlgorithmMenu.add(GrowTAlgorithm);
				AlgorithmMenu.add(ModPAlgorithm);
				AlgorithmMenu.add(ABAlgorithm);
				Menu OptionsMenu = new Menu("Options");
					MenuItem RestartMenu = new MenuItem("Restart");
					RestartMenu.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							GameState = 1;
							GamePanel.repaint();
							
						}
						
					});
					MenuItem MenuMenu = new MenuItem("Main Menu");
					MenuMenu.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {

							if(AutoCompleteRand.isRunning()){
								
								AutoCompleteRand.stop();
								SolveRandomlyMenu.setLabel("Solve Randomly");
								
							}
							if(AutoCompleteDFS.isRunning()){

								AutoCompleteDFS.stop();
								DFSMenu.setLabel("Solve with Depth-First Search");
								
							}
							if(AutoCompleteRB.isRunning()){

								AutoCompleteRB.stop();
								RBMenu.setLabel("Solve with Recursive Backtracker");
								
							}
							if(AutoCompleteGBFS.isRunning()){

								AutoCompleteGBFS.stop();
								GBFSMenu.setLabel("Solve with GBFS");
								
							}
							GameState = 0;
							MainFrame.setMenuBar(null);
							MainFrame.pack();
							GamePanel.repaint();
							
						}
						
					});
					MenuItem ExitMenu = new MenuItem("Exit");
					ExitMenu.addActionListener(new ActionListener(){

						@Override
						public void actionPerformed(ActionEvent AE) {
							
							System.exit(0);
							
						}
						
					});
				OptionsMenu.add(SolveRandomlyMenu);
				OptionsMenu.add(DFSMenu);
				OptionsMenu.add(RBMenu);
				OptionsMenu.add(GBFSMenu);
				OptionsMenu.add(RestartMenu);
				OptionsMenu.add(MenuMenu);
				OptionsMenu.add(ExitMenu);
				MainBar.add(AlgorithmMenu);	
				MainBar.add(DiffMenu);
				MainBar.add(OptionsMenu);
		
		MainFrame.pack();
		MainFrame.setVisible(true);
		
	}

	
	private static String GetDateTime(){
		
		return LocalDateTime.now().getYear() + "\\" + LocalDateTime.now().getMonthValue() + "\\" +
		LocalDateTime.now().getDayOfMonth() + "-" + LocalDateTime.now().getHour() + ":" +
		LocalDateTime.now().getMinute() + ":" + LocalDateTime.now().getSecond() + " : ";
		
	}
	
	private static void ExceptionHandler(Exception Exception, String FMsg, String SMsg){
		
		Exception.printStackTrace();
		JOptionPane.showMessageDialog(null, FMsg,
				"Error", JOptionPane.ERROR_MESSAGE);
		File LogFile = new File("./Log.txt");
			
		try {
			
			BufferedWriter LogWriter;
			if(LogFile.exists()){
				
				LogWriter = new BufferedWriter(new FileWriter(LogFile, true));
				
			}else{
				
				LogWriter = new BufferedWriter(new FileWriter(LogFile));
				
			}
			LogWriter.write(GetDateTime() + Exception.getMessage() + "\n\t");
			for(int i = 0; i < Exception.getStackTrace().length - 1; i++){

				LogWriter.write("at " + Exception.getStackTrace()[i] + "\n\t");
				
			}
			LogWriter.write("at " + Exception.getStackTrace()[Exception.getStackTrace().length - 1] + "\n");
			LogWriter.close();
			
		} catch (IOException Exception1) {
			
			Exception1.printStackTrace();	
			JOptionPane.showMessageDialog(null,
					SMsg, "Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(-1);
			
		}
		
	}
	
	private static void Setup(JComponent Origin){

		CellList = null;
		TargetCellCoordinates = null;
		ChainOfActionsGBFS = null;
		int Size;
		if(SizeOfGrid == 0){
			
			Size = MegaSize;
			
		}else if(SizeOfGrid == 1){
			
			Size = MediumSize;
			
		}else if(SizeOfGrid == 2){
			
			Size = SmallSize;
			
		}else{
			
			Size = XLSmallSize;
			
		}
		CellList = new ArrayList<ArrayList<Cell>>(GameSquare/Size);
		
		if(Algorithm.equals("Kruskal")){

			int SetIterator = 0; 
			for(int i = 0; i < GameSquare/Size; i++){
				
				CellList.add(new ArrayList<Cell>(GameSquare/Size));
				for(int j = 0; j < GameSquare/Size; j++){
					
					CellList.get(i).add(new Cell(Size, false, new Coordinate(j, i), new boolean[]{
							
							true, true, true, true
							
					}, false, false, false));
					SetIterator++;
					CellList.get(i).get(j).setSet(SetIterator);
					
				}
				
			}
			Kruskal(Size, Origin);	

		}else if(Algorithm.equals("Depth-First Search")){

			for(int i = 0; i < GameSquare/Size; i++){
			
				CellList.add(new ArrayList<Cell>(GameSquare/Size));
				for(int j = 0; j < GameSquare/Size; j++){
					
					CellList.get(i).add(new Cell(Size, false, new Coordinate(j, i), new boolean[]{
							
							true, true, true, true
							
					}, false, false, false));
					
				}
				
			}
			DepthFirstSearch(Size, Origin);
			
		}else if(Algorithm.equals("Modified Prim")){

			for(int i = 0; i < GameSquare/Size; i++){
			
				CellList.add(new ArrayList<Cell>(GameSquare/Size));
				for(int j = 0; j < GameSquare/Size; j++){
					
					CellList.get(i).add(new Cell(Size, false, new Coordinate(j, i), new boolean[]{
							
							true, true, true, true
							
					}, false, false, false));
					
				}
				
			}
			PrimMod(Size, Origin);
			
		}else if(Algorithm.equals("Aldous Broder")){

			for(int i = 0; i < GameSquare/Size; i++){
			
				CellList.add(new ArrayList<Cell>(GameSquare/Size));
				for(int j = 0; j < GameSquare/Size; j++){
					
					CellList.get(i).add(new Cell(Size, false, new Coordinate(j, i), new boolean[]{
							
							true, true, true, true
							
					}, false, false, false));
					
				}
				
			}
			AldousBroder(Size, Origin);
			
		}else if(Algorithm.equals("Growing Tree")){

			for(int i = 0; i < GameSquare/Size; i++){
			
				CellList.add(new ArrayList<Cell>(GameSquare/Size));
				for(int j = 0; j < GameSquare/Size; j++){
					
					CellList.get(i).add(new Cell(Size, false, new Coordinate(j, i), new boolean[]{
							
							true, true, true, true
							
					}, false, false, false));
					
				}
				
			}
			GrowT(Size, Origin);
			
		}else if(Algorithm.equals("Binary Tree")){

			for(int i = 0; i < GameSquare/Size; i++){
			
				CellList.add(new ArrayList<Cell>(GameSquare/Size));
				for(int j = 0; j < GameSquare/Size; j++){
					
					CellList.get(i).add(new Cell(Size, false, new Coordinate(j, i), new boolean[]{
							
							true, true, true, true
							
					}, false, false, false));
					
				}
				
			}
			BinaryT(Size, Origin);
			
		}
		Cell StartCell = CellList.get(Rand(0, CellList.size())).get(Rand(0, CellList.size())), TargetCell;
		do{
			
			TargetCell = CellList.get(Rand(0, CellList.size())).get(Rand(0, CellList.size()));
			
		}
		while(TargetCell.getLocalization().getX() == StartCell.getLocalization().getX() &
				TargetCell.getLocalization().getY() == StartCell.getLocalization().getY());
		CellList.get(StartCell.getLocalization().getY()).get(StartCell.getLocalization().getX()).setStart(true);
		CellList.get(TargetCell.getLocalization().getY()).get(TargetCell.getLocalization().getX()).setTarget(true);
		TargetCellCoordinates = new Coordinate(TargetCell.getLocalization().getX(), TargetCell.getLocalization().getY());
		CurrentCellInGame = StartCell;
		
		MainFrame.setMenuBar(MainBar);
		MainFrame.pack();
		
	}
	
	public static void BinaryT(int Size, JComponent Origin){
		
		for(int TempY = 0; TempY < GameSquare/Size; TempY++){
			
			for(int TempX = 0; TempX < GameSquare/Size; TempX++){
				
				Cell Current = CellList.get(TempY).get(TempX);
				CellList.get(TempY).get(TempX).setVisited(true);
				ArrayList<Cell> PosiblesRoutes = new ArrayList<Cell>(4);
				if(TempX != 0){

					PosiblesRoutes.add(CellList.get(TempY).get(TempX-1));
					
				}
				if(TempY != 0){

					PosiblesRoutes.add(CellList.get(TempY-1).get(TempX));
					
				}
				CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).setVisited(true);
				
				if(PosiblesRoutes.size() != 0){
					
					Cell Previus = Current;
					Current = PosiblesRoutes.get((PosiblesRoutes.size() != 1)?Rand(0, PosiblesRoutes.size()):0);
					if(Current.getLocalization().getX() == Previus.getLocalization().getX()-1){

						CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetLeftWall(false);
						CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetRightWall(false);
						
					}else if(Current.getLocalization().getX() == Previus.getLocalization().getX()+1){

						CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetRightWall(false);
						CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetLeftWall(false);
						
					}else if(Current.getLocalization().getY() == Previus.getLocalization().getY()-1){

						CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetTopWall(false);
						CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetBottomWall(false);
						
					}else{
						
						CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetBottomWall(false);
						CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetTopWall(false);
						
					}
					
				}
				
			}
			
		}
		
		GameState = 2;
		if(Origin != null){

			Origin.repaint();	

		}
		
	}
	
	public static void GrowT(int Size, JComponent Origin){
		
		ArrayList<Cell> TempList = new ArrayList<Cell>((int) Math.pow(GameSquare/Size, 2));
		Cell Current = CellList.get(Rand(0, GameSquare/Size)).get(Rand(0, GameSquare/Size));
		CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).setVisited(true);
		TempList.add(Current);
		
		while(TempList.size() != 0){
			
			//Random Selection : Prim, Newest Selection : DFS, Old Selection : Something Strange :b
			int Rand = /*0;*/TempList.size()-1;/*(TempList.size() != 1)?Rand(0, TempList.size()):0;*/
			Current = TempList.get(Rand);
			int TempX = Current.getLocalization().getX(), TempY = Current.getLocalization().getY();
			ArrayList<Cell> PosiblesRoutes = new ArrayList<Cell>(4);
			if(TempX != 0){

				if(!CellList.get(TempY).get(TempX-1).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY).get(TempX-1));
					
				}
				
			}
			if(TempX != GameSquare/Size - 1){

				if(!CellList.get(TempY).get(TempX+1).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY).get(TempX+1));
					
				}
				
			}
			if(TempY != 0){

				if(!CellList.get(TempY-1).get(TempX).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY-1).get(TempX));
					
				}
				
			}
			if(TempY != GameSquare/Size - 1){

				if(!CellList.get(TempY+1).get(TempX).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY+1).get(TempX));
					
				}
				
			}
			CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).setVisited(true);
			
			if(PosiblesRoutes.size() != 0){
				
				int Selection = (PosiblesRoutes.size() != 1)?Rand(0, PosiblesRoutes.size()):0;
				TempList.add(PosiblesRoutes.get(Selection));
				CellList.get(PosiblesRoutes.get(Selection).getLocalization().getY()).get(PosiblesRoutes.get(Selection).getLocalization().getX()).setVisited(true);
				if(Current.getLocalization().getX() == PosiblesRoutes.get(Selection).getLocalization().getX()-1){

					TempList.get(TempList.size()-1).SetLeftWall(false);
					TempList.get(Rand).SetRightWall(false);
					
				}else if(Current.getLocalization().getX() == PosiblesRoutes.get(Selection).getLocalization().getX()+1){

					TempList.get(TempList.size()-1).SetRightWall(false);
					TempList.get(Rand).SetLeftWall(false);
					
				}else if(Current.getLocalization().getY() == PosiblesRoutes.get(Selection).getLocalization().getY()-1){

					TempList.get(TempList.size()-1).SetTopWall(false);
					TempList.get(Rand).SetBottomWall(false);
					
				}else{
					
					TempList.get(TempList.size()-1).SetBottomWall(false);
					TempList.get(Rand).SetTopWall(false);
					
				}
				
			}else{
				
				CellList.get(TempList.get(Rand).getLocalization().getY()).set(TempList.get(Rand).getLocalization().getX(), TempList.get(Rand));
				TempList.remove(Rand);
				
			}
			
		}
		
		GameState = 2;
		if(Origin != null){

			Origin.repaint();	

		}
		
	}
	
	public static void AldousBroder(int Size, JComponent Origin){
		
		Cell Current = CellList.get(Rand(0, CellList.size())).get(Rand(0, CellList.size()));
		Cell Previus = null;
		int Visiteds = 0;
		
		while(true){
			
			int TempX = Current.getLocalization().getX(), TempY = Current.getLocalization().getY();
			ArrayList<Cell> PosiblesRoutes = new ArrayList<Cell>(4);
			if(TempX != 0){

				PosiblesRoutes.add(CellList.get(TempY).get(TempX-1));
				
			}
			if(TempX != GameSquare/Size - 1){

				PosiblesRoutes.add(CellList.get(TempY).get(TempX+1));
				
			}
			if(TempY != 0){

				PosiblesRoutes.add(CellList.get(TempY-1).get(TempX));	
				
			}
			if(TempY != GameSquare/Size - 1){

				PosiblesRoutes.add(CellList.get(TempY+1).get(TempX));
				
			}
			
			Previus = Current;
			Current = PosiblesRoutes.get((PosiblesRoutes.size() != 1)?Rand(0, PosiblesRoutes.size()):0);
			if(!Current.getVisited()){
				
				Visiteds++;
				CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).setVisited(true);
				if(Current.getLocalization().getX() == Previus.getLocalization().getX()-1){

					CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetLeftWall(false);
					CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetRightWall(false);
					
				}else if(Current.getLocalization().getX() == Previus.getLocalization().getX()+1){

					CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetRightWall(false);
					CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetLeftWall(false);
					
				}else if(Current.getLocalization().getY() == Previus.getLocalization().getY()-1){

					CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetTopWall(false);
					CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetBottomWall(false);
					
				}else{
					
					CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetBottomWall(false);
					CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetTopWall(false);
					
				}
				
			}
			if(Visiteds == (int)Math.pow(GameSquare/Size, 2)){
				
				break;
				
			}
			
		}
		
		GameState = 2;
		if(Origin != null){

			Origin.repaint();	

		}
		
	}
	
	public static void DepthFirstSearch(int Size, JComponent Origin){
		
		Cell Current = CellList.get(Rand(0, CellList.size())).get(Rand(0, CellList.size()));
		Cell Previus = null;
		
		while(true){
			
			int TempX = Current.getLocalization().getX(), TempY = Current.getLocalization().getY();
			ArrayList<Cell> PosiblesRoutes = new ArrayList<Cell>(4);
			if(TempX != 0){

				if(!CellList.get(TempY).get(TempX-1).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY).get(TempX-1));
					
				}else if(Previus != null){
					
					if(TempX-1 == Previus.getLocalization().getX() & TempY == Previus.getLocalization().getY()){
						
						CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetRightWall(false);
						CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetLeftWall(false);
						
					}
					
				}
				
			}
			if(TempX != GameSquare/Size - 1){

				if(!CellList.get(TempY).get(TempX+1).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY).get(TempX+1));
					
				}else if(Previus != null){
					
					if(TempX+1 == Previus.getLocalization().getX() & TempY == Previus.getLocalization().getY()){
						
						CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetLeftWall(false);
						CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetRightWall(false);
						
					}
					
				}
				
			}
			if(TempY != 0){

				if(!CellList.get(TempY-1).get(TempX).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY-1).get(TempX));
					
				}else if(Previus != null){
					
					if(TempX == Previus.getLocalization().getX() & TempY-1 == Previus.getLocalization().getY()){
						
						CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetBottomWall(false);
						CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetTopWall(false);
						
					}
					
				}
				
			}
			if(TempY != GameSquare/Size - 1){

				if(!CellList.get(TempY+1).get(TempX).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY+1).get(TempX));
					
				}else if(Previus != null){
					
					if(TempX == Previus.getLocalization().getX() & TempY+1 == Previus.getLocalization().getY()){
						
						CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetTopWall(false);
						CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetBottomWall(false);
						
					}
					
				}
				
			}
			CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).setVisited(true);
			
			if(PosiblesRoutes.size() != 0){
	
				Previus = Current;
				Current = PosiblesRoutes.get((PosiblesRoutes.size() != 1)?Rand(0, PosiblesRoutes.size()):0);
				if(Current.getLocalization().getX() == Previus.getLocalization().getX()-1){

					CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetLeftWall(false);
					CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetRightWall(false);
					
				}else if(Current.getLocalization().getX() == Previus.getLocalization().getX()+1){

					CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetRightWall(false);
					CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetLeftWall(false);
					
				}else if(Current.getLocalization().getY() == Previus.getLocalization().getY()-1){

					CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetTopWall(false);
					CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetBottomWall(false);
					
				}else{
					
					CellList.get(Previus.getLocalization().getY()).get(Previus.getLocalization().getX()).SetBottomWall(false);
					CellList.get(Current.getLocalization().getY()).get(Current.getLocalization().getX()).SetTopWall(false);
					
				}
				
			}else{
				
				boolean MoreSpace = false;
				TempFor : for(ArrayList<Cell> TempArray : CellList){
					
					for(Cell TempCell : TempArray){
						
						if(!TempCell.getVisited()){
							
							MoreSpace = true;
							break TempFor;
							
						}
						
					}
					
				}
				if(!MoreSpace){
					
					break;
					
				}
				
				Current = null;
				
				while(true){
					
					do{

						Current = CellList.get(Rand(0, GameSquare/Size)).get(Rand(0, GameSquare/Size));	
						
					}while(!Current.getVisited());
					
					TempX = Current.getLocalization().getX();
					TempY = Current.getLocalization().getY();
					CellList.get(TempY).get(TempX).setVisited(true);
					if(TempX != 0){

						if(!CellList.get(TempY).get(TempX-1).getVisited()){
							
							break;
							
						}
						
					}
					if(TempX != GameSquare/Size - 1){

						if(!CellList.get(TempY).get(TempX+1).getVisited()){
							
							break;
							
						}
						
					}
					if(TempY != 0){

						if(!CellList.get(TempY-1).get(TempX).getVisited()){
							
							break;
							
						}
						
					}
					if(TempY != GameSquare/Size - 1){

						if(!CellList.get(TempY+1).get(TempX).getVisited()){
							
							break;
							
						}
						
					}
					
				}
				
			}
			
		}
		
		GameState = 2;
		if(Origin != null){

			Origin.repaint();	

		}
		
	}
	
	public static void Kruskal(int Size, JComponent Origin){

		int SetIterator = (int) Math.pow(GameSquare/Size, 2); 
		
		while(SetIterator != 1){
			
			Cell TempCell = CellList.get(Rand(0, GameSquare/Size)).get(Rand(0, GameSquare/Size));
			int TempX = TempCell.getLocalization().getX(), TempY = TempCell.getLocalization().getY();

			CellList.get(TempY).get(TempX).setVisited(true);
			
			ArrayList<Cell> PosiblesDirections = new ArrayList<Cell>(4);

			if(TempX != 0){
				
				if(CellList.get(TempY).get(TempX-1).getSet() != TempCell.getSet()){
					
					PosiblesDirections.add(CellList.get(TempY).get(TempX-1));
					
				}
				
			}
			if(TempY != 0){

				if(CellList.get(TempY-1).get(TempX).getSet() != TempCell.getSet()){
					
					PosiblesDirections.add(CellList.get(TempY-1).get(TempX));
					
				}
						
			}
			if(TempX != GameSquare/Size-1){

				if(CellList.get(TempY).get(TempX+1).getSet() != TempCell.getSet()){
					
					PosiblesDirections.add(CellList.get(TempY).get(TempX+1));

				}
				
			}
			if(TempY != GameSquare/Size-1){

				if(CellList.get(TempY+1).get(TempX).getSet() != TempCell.getSet()){
					
					PosiblesDirections.add(CellList.get(TempY+1).get(TempX));
					
				}
				
			}
			
			if(PosiblesDirections.size() >= 1){
				
				Cell CellDir = PosiblesDirections.get((PosiblesDirections.size() > 1)?Rand(0, PosiblesDirections.size()):0);
				int TempSet = CellDir.getSet();
				for(int i = 0; i < GameSquare/Size; i++){
					
					for(int j = 0; j < GameSquare/Size; j++){
				
						if(CellList.get(i).get(j).getSet() == TempSet){

							CellList.get(i).get(j).setSet(TempCell.getSet());	
							
						}
						
					}
					
				}
				SetIterator--;
				if(CellDir.getLocalization().getX() == TempX-1){

					CellList.get(TempCell.getLocalization().getY()).get(TempCell.getLocalization().getX()).SetLeftWall(false);
					CellList.get(CellDir.getLocalization().getY()).get(CellDir.getLocalization().getX()).SetRightWall(false);
					
				}else if(CellDir.getLocalization().getX() == TempX+1){

					CellList.get(TempCell.getLocalization().getY()).get(TempCell.getLocalization().getX()).SetRightWall(false);
					CellList.get(CellDir.getLocalization().getY()).get(CellDir.getLocalization().getX()).SetLeftWall(false);
					
				}else if(CellDir.getLocalization().getY() == TempY-1){

					CellList.get(TempCell.getLocalization().getY()).get(TempCell.getLocalization().getX()).SetTopWall(false);
					CellList.get(CellDir.getLocalization().getY()).get(CellDir.getLocalization().getX()).SetBottomWall(false);
					
				}else if(CellDir.getLocalization().getY() == TempY+1){
					
					CellList.get(TempCell.getLocalization().getY()).get(TempCell.getLocalization().getX()).SetBottomWall(false);
					CellList.get(CellDir.getLocalization().getY()).get(CellDir.getLocalization().getX()).SetTopWall(false);
					
				}
				
			}

		}
		
		GameState = 2;
		if(Origin != null){

			Origin.repaint();	

		}
		
	}
	
	public static void PrimMod(int Size, JComponent Origin){

		ArrayList<Cell> Frontiers = new ArrayList<Cell>((int) Math.pow(GameSquare/Size, 2));
		Cell CurrentCell = CellList.get(Rand(0, GameSquare/Size)).get(Rand(0, GameSquare/Size));
		CellList.get(CurrentCell.getLocalization().getY()).get(CurrentCell.getLocalization().getX()).setVisited(true);
		CurrentCell.setVisited(true);
		if(CurrentCell.getLocalization().getX() != 0){
			
			Frontiers.add(CellList.get(CurrentCell.getLocalization().getY()).get(CurrentCell.getLocalization().getX()-1));
			
		}
		if(CurrentCell.getLocalization().getX() != GameSquare/Size-1){
			
			Frontiers.add(CellList.get(CurrentCell.getLocalization().getY()).get(CurrentCell.getLocalization().getX()+1));
			
		}
		if(CurrentCell.getLocalization().getY() != 0){
			
			Frontiers.add(CellList.get(CurrentCell.getLocalization().getY()-1).get(CurrentCell.getLocalization().getX()));
			
		}
		if(CurrentCell.getLocalization().getY() != GameSquare/Size-1){
			
			Frontiers.add(CellList.get(CurrentCell.getLocalization().getY()+1).get(CurrentCell.getLocalization().getX()));
			
		}
		
		while(Frontiers.size() != 0){

			int RandTemp = (Frontiers.size() > 1)?Rand(0, Frontiers.size()):0;
			Cell TempCell = Frontiers.get(RandTemp);
			Frontiers.remove(RandTemp);
			int TempX = TempCell.getLocalization().getX(), TempY = TempCell.getLocalization().getY();
			CellList.get(TempY).get(TempX).setVisited(true);
			ArrayList<Cell> PosiblesRoutes = new ArrayList<Cell>(4);
			if(TempX != 0){ 
				
				if(CellList.get(TempY).get(TempX-1).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY).get(TempX-1));
					
				}else{

					boolean IsOldFrontier = false;
					TempFor : for(Cell FrontTemp : Frontiers){
						
						if(FrontTemp.getLocalization().getX() == TempX-1 & 
								FrontTemp.getLocalization().getY() == TempY){
							
							IsOldFrontier = true;
							break TempFor;
							
						}
						
					}
					if(!IsOldFrontier){
						
						Frontiers.add(CellList.get(TempY).get(TempX-1));
						
					}
					
				}
				
			}
			if(TempX != GameSquare/Size-1){

				if(CellList.get(TempY).get(TempX+1).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY).get(TempX+1));
					
				}else{

					boolean IsOldFrontier = false;
					TempFor : for(Cell FrontTemp : Frontiers){
						
						if(FrontTemp.getLocalization().getX() == TempX+1 & 
								FrontTemp.getLocalization().getY() == TempY){
							
							IsOldFrontier = true;
							break TempFor;
							
						}
						
					}
					if(!IsOldFrontier){
						
						Frontiers.add(CellList.get(TempY).get(TempX+1));
						
					}
					
				}
				
			}
			if(TempY != 0){ 

				if(CellList.get(TempY-1).get(TempX).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY-1).get(TempX));
					
				}else{

					boolean IsOldFrontier = false;
					TempFor : for(Cell FrontTemp : Frontiers){
						
						if(FrontTemp.getLocalization().getX() == TempX & 
								FrontTemp.getLocalization().getY() == TempY-1){
							
							IsOldFrontier = true;
							break TempFor;
							
						}
						
					}
					if(!IsOldFrontier){
						
						Frontiers.add(CellList.get(TempY-1).get(TempX));
						
					}
					
				}
				
			}
			if(TempY != GameSquare/Size-1){

				if(CellList.get(TempY+1).get(TempX).getVisited()){
					
					PosiblesRoutes.add(CellList.get(TempY+1).get(TempX));
					
				}else{

					boolean IsOldFrontier = false;
					TempFor : for(Cell FrontTemp : Frontiers){
						
						if(FrontTemp.getLocalization().getX() == TempX & 
								FrontTemp.getLocalization().getY() == TempY+1){
							
							IsOldFrontier = true;
							break TempFor;
							
						}
						
					}
					if(!IsOldFrontier){
						
						Frontiers.add(CellList.get(TempY+1).get(TempX));
						
					}
					
				}
				
			}
			int Dir = (PosiblesRoutes.size() > 1)?Rand(0, PosiblesRoutes.size()):0;
			if(TempX-1 == PosiblesRoutes.get(Dir).getLocalization().getX() & 
					TempY == PosiblesRoutes.get(Dir).getLocalization().getY()){
				
				CellList.get(TempY).get(TempX).SetLeftWall(false);
				CellList.get(PosiblesRoutes.get(Dir).getLocalization().getY()).get(PosiblesRoutes.get(Dir).getLocalization().getX()).SetRightWall(false);
				
			}else if(TempX+1 == PosiblesRoutes.get(Dir).getLocalization().getX() & 
					TempY == PosiblesRoutes.get(Dir).getLocalization().getY()){

				CellList.get(TempY).get(TempX).SetRightWall(false);
				CellList.get(PosiblesRoutes.get(Dir).getLocalization().getY()).get(PosiblesRoutes.get(Dir).getLocalization().getX()).SetLeftWall(false);
				
			}else if(TempX == PosiblesRoutes.get(Dir).getLocalization().getX() & 
					TempY-1 == PosiblesRoutes.get(Dir).getLocalization().getY()){

				CellList.get(TempY).get(TempX).SetTopWall(false);
				CellList.get(PosiblesRoutes.get(Dir).getLocalization().getY()).get(PosiblesRoutes.get(Dir).getLocalization().getX()).SetBottomWall(false);
				
			}else if(TempX == PosiblesRoutes.get(Dir).getLocalization().getX() & 
					TempY+1 == PosiblesRoutes.get(Dir).getLocalization().getY()){

				CellList.get(TempY).get(TempX).SetBottomWall(false);
				CellList.get(PosiblesRoutes.get(Dir).getLocalization().getY()).get(PosiblesRoutes.get(Dir).getLocalization().getX()).SetTopWall(false);
				
			}
			
		}
		
		GameState = 2;
		if(Origin != null){

			Origin.repaint();	

		}
		
	}
	
	public static int Rand(int Min, int Max){
		
		return ThreadLocalRandom.current().nextInt(Min, Max);
		
	}
	
	public static void GBFS(){
		
		Set<Cell> ClosedSet = new HashSet<Cell>(30);
		Set<Cell> OpenSet = new HashSet<Cell>(10);
		OpenSet.add(CurrentCellInGame);
		Map<Cell, Cell> ComesFrom = new HashMap<Cell, Cell>(30);
		Cell CurrentCell = CurrentCellInGame;
		while(true){
			
			if(CurrentCell.isTarget()) break;
			int TempX = CurrentCell.getLocalization().getX(), TempY = CurrentCell.getLocalization().getY();
			if(TempX != 0){
				
				if(!CurrentCell.getLeftWall()){
					
					if(!ClosedSet.contains(CellList.get(TempY).get(TempX-1))){
						
						OpenSet.add(CellList.get(TempY).get(TempX-1));
						ComesFrom.put(CellList.get(TempY).get(TempX-1), CurrentCell);
						
					}
					
				}
				
			}
			if(TempY != GameSquare/CurrentCell.getSize()-1){

				if(!CurrentCell.getBottomWall()){
					
					if(!ClosedSet.contains(CellList.get(TempY+1).get(TempX))){

						OpenSet.add(CellList.get(TempY+1).get(TempX));
						ComesFrom.put(CellList.get(TempY+1).get(TempX), CurrentCell);
						
					}
					
				}
				
			}
			if(TempX != GameSquare/CurrentCell.getSize()-1){
				
				if(!CurrentCell.getRightWall()){
					
					if(!ClosedSet.contains(CellList.get(TempY).get(TempX+1))){

						OpenSet.add(CellList.get(TempY).get(TempX+1));	
						ComesFrom.put(CellList.get(TempY).get(TempX+1), CurrentCell);
						
					}
					
				}
				
			}
			if(TempY != 0){

				if(!CurrentCell.getTopWall()){
					
					if(!ClosedSet.contains(CellList.get(TempY-1).get(TempX))){

						OpenSet.add(CellList.get(TempY-1).get(TempX));	
						ComesFrom.put(CellList.get(TempY-1).get(TempX), CurrentCell);
						
					}
					
				}
				
			}
			ClosedSet.add(CurrentCell);
			OpenSet.remove(CurrentCell);
			Cell TempHCell = null;
			for(Cell Iterated : OpenSet){
				
				if(TempHCell == null){
					
					TempHCell = Iterated;
					continue;
					
				}
				if(Math.max(Heurastic1(Iterated), Heurastic2(Iterated)) < Math.max(Heurastic1(TempHCell), Heurastic2(TempHCell))){
					
					TempHCell = Iterated; 
					
				}
				
			}
			CurrentCell = TempHCell;
			
		}
		ArrayList<Cell> TempChainOfActions = new ArrayList<Cell>(40);
		while(true){

			CurrentCell = ComesFrom.get(CurrentCell);
			if(CurrentCell.equals(CurrentCellInGame)){
				
				break;
				
			}
			TempChainOfActions.add(0, CurrentCell);
			
		}
		ChainOfActionsGBFS = TempChainOfActions;
		
	}
	
	public static int Heurastic1(Cell Node){
		
		int TempXH, TempYH;
		int TNX = TargetCellCoordinates.getX();
		int CNX = Node.getLocalization().getX();
		int TNY = TargetCellCoordinates.getY();
		int CNY = Node.getLocalization().getY(); 
		if(CNX > TNX){
			
			TempXH = CNX - TNX;
			
		}else{
			
			TempXH = TNX - CNX;
			
		} 
		if(CNY > TNY){
			
			TempYH = CNY - TNY;
			
		}else{
			
			TempYH = TNY - CNY;
			
		}
		return (TempYH + TempXH);
		
	}

	public static int Heurastic2(Cell Node){
		
		int TempXH, TempYH;
		int TNX = TargetCellCoordinates.getX();
		int CNX = Node.getLocalization().getX();
		int TNY = TargetCellCoordinates.getY();
		int CNY = Node.getLocalization().getY(); 
		if(CNX > TNX){
			
			TempXH = CNX - TNX;
			
		}else{
			
			TempXH = TNX - CNX;
			
		} 
		if(CNY > TNY){
			
			TempYH = CNY - TNY;
			
		}else{
			
			TempYH = TNY - CNY;
			
		}
		return ((int)Math.round(Math.sqrt(Math.pow(TempYH, 2) + Math.pow(TempXH, 2))));
		
	}
	
}

class Coordinate{
	
	Coordinate(int x, int y){
		
		this.setX(x);
		this.setY(y);
		
	}
	
	public int getX() {
		
		return x;
		
	}
	
	public void setX(int x) {
		
		this.x = x;
		
	}

	public int getY() {
		
		return y;
		
	}

	public void setY(int y) {
		
		this.y = y;
		
	}

	private int x;
	private int y;
	
}