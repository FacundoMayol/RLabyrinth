package mainPackage;

public class Cell {

Cell(int Size, boolean Visited, Coordinate Localization, boolean[] Walls, boolean Start, boolean Target, boolean VisitedInGame){
		
		this.setSize(Size);
		this.setVisited(Visited);
		this.setLocalization(Localization);
		this.setWalls(Walls);
		this.setStart(Start);
		this.setTarget(Target);
		this.setVisitedInGame(VisitedInGame);
		
	}
	
	public int getSize() {
		
		return Size;
		
	}

	public void setSize(int size) {
		
		Size = size;
		
	}

	public boolean getVisited() {
		
		return Visited;
		
	}

	public void setVisited(boolean visited) {
		
		Visited = visited;
		
	}
	
	public Coordinate getLocalization() {
		
		return Localization;
		
	}

	public void setLocalization(Coordinate localization) {
		
		this.Localization = localization;
		
	}

	public boolean[] getWalls() {
		
		return new boolean[]{
			
			TopWall, RightWall, BottomWall, LeftWall 	
			
		};
		
	}

	public boolean getRightWall() {
		
		return RightWall;
		
	}

	public boolean getLeftWall() {
		
		return LeftWall;
		
	}

	public boolean getBottomWall() {
		
		return BottomWall;
		
	}

	public void SetTopWall(boolean TopWall) {
		
		this.TopWall = TopWall;
		
	}

	public void SetBottomWall(boolean BottomWall) {
		
		this.BottomWall = BottomWall;
		
	}

	public void SetLeftWall(boolean LeftWall) {
		
		this.LeftWall = LeftWall;
		
	}

	public void SetRightWall(boolean RightWall) {
		
		this.RightWall = RightWall;
		
	}

	public boolean getTopWall() {
		
		return TopWall;
		
	}

	public void setWalls(boolean[] walls) {
		
		TopWall = walls[0];
		RightWall = walls[1];
		BottomWall = walls[2];
		LeftWall = walls[3];
		
	}

	public boolean isStart() {
		
		return Start;
		
	}

	public void setStart(boolean start) {
		
		Start = start;
		VisitedInGame = true;
		
	}

	public boolean isTarget() {
		
		return Target;
		
	}

	public void setTarget(boolean target) {
		
		Target = target;
		
	}

	public boolean isVisitedInGame() {
		
		return VisitedInGame;
		
	}

	public void setVisitedInGame(boolean visitedInGame) {
		
		VisitedInGame = visitedInGame;
		
	}

	public int getSet() {
		
		return Set;
		
	}

	public void setSet(int set) {
		
		Set = set;
		
	}
	
	@Override 
	public boolean equals(Object Temp){
		
		Cell OCell = (Cell) Temp; 
		if(this.getLocalization().getX() == OCell.getLocalization().getX() && 
				this.getLocalization().getY() == OCell.getLocalization().getY()){
			
			return true;
			
		}
		return false;
		
	}

	private boolean Visited, VisitedInGame;
	private int Size, Set = 0;
	private Coordinate Localization;
	private boolean TopWall = false, BottomWall = false, LeftWall = false, RightWall = false, Start = false, Target = false;
	
}
