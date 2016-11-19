/*
 * This program creates a changeable maze. The user must get from 
 * the left side to the right side without using their move limit.
 */

import java.util.ArrayList;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class ImLost extends Application {
	static Circle spaceShip;
	static ArrayList<PathWall> pathWalls;
	static int i, j;
	static int iteration;
	static int numberOfMoves; /*
								 * This is the number of moves in the current
								 * round
								 */
	static int score;

	public static void main(String[] args) {
		score = 0;
		pathWalls = new ArrayList<PathWall>();
		JOptionPane.showMessageDialog(null, "Press the 'A' key to surrender and start over.");
		JOptionPane.showMessageDialog(null,
				"Keep in mind that your move count for that particular round does not restart if you surrender.");
		launch(args);
	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void start(Stage primaryStage) {
		iteration = 0;
		// Create the horizontal path walls.
		for (i = 0; i < 11; ++i)
			for (j = 0; j < 7; ++j) {
				double x = 50.0 + (100.0 * i);
				double y = 50.0 + (100.0 * j);
				pathWalls.add(pathWalls.size(), new PathWall(x, y, 'r'));
				pathWalls.get(iteration++).setFill(Color.LIMEGREEN);
				pathWalls.get(iteration - 1).setStroke(Color.AQUA);
			}
		// Create the vertical path walls.
		for (i = 0; i < 12; ++i)
			for (j = 0; j < 6; ++j) {
				double x = 50.0 + (100.0 * i);
				double y = 50.0 + (100.0 * j);
				pathWalls.add(pathWalls.size(), new PathWall(x, y, 'd'));
				pathWalls.get(iteration++).setFill(Color.LIMEGREEN);
				pathWalls.get(iteration - 1).setFill(Color.AQUA);
			}
		Rectangle topBorder = new Rectangle(50.0, 0.0, 1100.0, 50.0);
		topBorder.setFill(Color.DARKGRAY);
		topBorder.setStroke(Color.ORANGERED);
		Rectangle bottomBorder = new Rectangle(50.0, 650.0, 1100.0, 650.0);
		bottomBorder.setFill(Color.DARKGRAY);
		bottomBorder.setStroke(Color.ORANGERED);
		char[][] doorSwitchCode = new char[9][6];/*
													 * These codes determine
													 * that depending on which
													 * direction the user enters
													 * the box, which of the
													 * box's doors open and
													 * close.
													 */
		for (i = 0; i < doorSwitchCode.length; ++i)
			for (j = 0; j < doorSwitchCode[i].length; ++j) {
				byte determinant = (byte) (Math.random() * 3);
				switch (determinant) {
				case 0:
					doorSwitchCode[i][j] = 'L';
					break;
				case 1:
					doorSwitchCode[i][j] = 'U';
					break;
				case 2:
					doorSwitchCode[i][j] = 'R';
					break;
				}
			}
		char[] doorOpenCodes = new char[4];
		doorOpenCodes[0] = 'U';
		doorOpenCodes[1] = 'R';
		doorOpenCodes[2] = 'D';
		doorOpenCodes[3] = 'L';
		char[][] doorOpenCode = new char[9][6];
		for (i = 0; i < doorOpenCode.length; ++i)
			for (j = 0; j < doorOpenCode[i].length; ++j) {
				byte determinant = (byte) (Math.random() * 4);
				doorOpenCode[i][j] = doorOpenCodes[determinant];
			}
		short numberOfOpenings = 0;
		for (i = 0; i < 2; ++i)
			for (j = 0; j < 6; ++j) {
				if (doorOpenCode[i][j] == 'L')
					++numberOfOpenings;
				if (i == 0
						&& !(numberOfOpenings >= 1)) {/*
														 * The user must atleast
														 * be able to enter the
														 * maze for the game to
														 * be considered fair.
														 */
					start(new Stage());
					return;
				}
				if (i == 1 && !(numberOfOpenings >= 1)) {
					start(new Stage());
					return;
				}
			}
		boolean[] wallOpen = new boolean[pathWalls.size()];
		for (int n = 0; n < wallOpen.length; ++n)
			wallOpen[n] = true;
		boolean[][] playerInsideBox = new boolean[11][6];
		for (i = 0; i < playerInsideBox.length; ++i)
			for (j = 0; j < playerInsideBox[i].length; ++j)
				playerInsideBox[i][j] = false;
		spaceShip = new Circle(20.0, 320.0, 15.0);
		spaceShip.setFill(Color.GREEN);
		spaceShip.setStroke(Color.LIGHTCORAL);
		Pane primaryPane = new Pane();
		for (PathWall pw : pathWalls)
			primaryPane.getChildren().add(pw);
		primaryPane.getChildren().add(spaceShip);
		primaryPane.getChildren().add(topBorder);
		primaryPane.getChildren().add(bottomBorder);
		Scene primaryScene = new Scene(primaryPane);
		primaryScene.setFill(Color.BLACK);
		primaryStage.setScene(primaryScene);
		primaryStage.show();
		primaryScene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case U:
			case UP:
				spaceShip.setCenterY(spaceShip.getCenterY() - 6.0);
				break;
			case L:
			case LEFT:
				spaceShip.setCenterX(spaceShip.getCenterX() - 6.0);
				break;
			case D:
			case DOWN:
				spaceShip.setCenterY(spaceShip.getCenterY() + 6.0);
				break;
			case R:
			case RIGHT:
				spaceShip.setCenterX(spaceShip.getCenterX() + 6.0);
				break;
			}
			switch (e.getCode()) {
			case U:
			case UP:
			case L:
			case LEFT:
			case D:
			case DOWN:
			case R:
			case RIGHT:
				/**
				 * The number of moves should be incremented every time the user
				 * presses a key to move up, down, left, or right.
				 */
				++numberOfMoves;
				if (numberOfMoves > 500) {
					JOptionPane.showMessageDialog(null, "Sorry, you've reached you move limit of 500.");
					JOptionPane.showMessageDialog(null, "-100 points.");
					score -= 100;
					numberOfMoves = 0;
					primaryStage.close();
					start(new Stage());
				}
				if (spaceShip.getCenterX() > 1200.0) {
					JOptionPane.showMessageDialog(null, "Congrats! You made it across!");
					JOptionPane.showMessageDialog(null, "It took you " + numberOfMoves + " moves.");
					score += 500 - numberOfMoves;
					JOptionPane.showMessageDialog(null, "+" + (500 - numberOfMoves) + " points.");
					JOptionPane.showMessageDialog(null, "Your score is now " + score + ".");
					numberOfMoves = 0;
					primaryStage.close();
					start(new Stage());
				}
				if (spaceShip.getCenterX() < 10.0)
					spaceShip.setCenterX(22.0);
				if (spaceShip.getCenterY() < 10.0)
					spaceShip.setCenterY(22.0);
				if (spaceShip.getCenterY() > 720.)
					spaceShip.setCenterY(700.0);
				for (int n = 0; n < pathWalls.size(); ++n)
					if (pathWalls.get(n).contains(new Point2D(spaceShip.getCenterX(), spaceShip.getCenterY())))
						if (!wallOpen[n])
							switch (e.getCode()) {
							case U:
							case UP:
								spaceShip.setCenterY(spaceShip.getCenterY() + 8.0);
								break;
							case R:
							case RIGHT:
								spaceShip.setCenterX(spaceShip.getCenterX() - 8.0);
								break;
							case D:
							case DOWN:
								spaceShip.setCenterY(spaceShip.getCenterY() - 8.0);
								break;
							case L:
							case LEFT:
								spaceShip.setCenterX(spaceShip.getCenterX() + 8.0);
							}
				if (topBorder.contains(new Point2D(spaceShip.getCenterX(), spaceShip.getCenterY()))
						|| bottomBorder.contains(new Point2D(spaceShip.getCenterX(), spaceShip.getCenterY())))
					switch (e.getCode()) {
					case U:
					case UP:
						spaceShip.setCenterY(spaceShip.getCenterY() + 8.0);
						break;
					case R:
					case RIGHT:
						spaceShip.setCenterX(spaceShip.getCenterX() - 8.0);
						break;
					case D:
					case DOWN:
						spaceShip.setCenterY(spaceShip.getCenterY() - 8.0);
						break;
					case L:
					case LEFT:
						spaceShip.setCenterX(spaceShip.getCenterX() + 8.0);
					}
				for (i = 0; i < 9; ++i)
					for (j = 0; j < 6; ++j) {
						int i0 = ((i + 1) * 7) + j;
						int i1 = i0 + 82 - i;
						int i2 = i0 + 1;
						int i3 = i0 + 76 - i;
						pathWalls.get(i0).setFill(Color.ORANGERED);
						pathWalls.get(i0).setStroke(Color.ORANGERED);
						pathWalls.get(i1).setFill(Color.ORANGERED);
						pathWalls.get(i1).setStroke(Color.ORANGERED);
						pathWalls.get(i2).setFill(Color.ORANGERED);
						pathWalls.get(i2).setStroke(Color.ORANGERED);
						pathWalls.get(i3).setFill(Color.ORANGERED);
						pathWalls.get(i3).setStroke(Color.ORANGERED);
						wallOpen[i0] = false;
						wallOpen[i1] = false;
						wallOpen[i2] = false;
						wallOpen[i3] = false;
					}
				int index = 0;
				for (i = 0; i < 9; ++i)
					for (j = 0; j < 6; ++j) {
						switch (doorOpenCode[i][j]) {
						case 'U':
							index = ((i + 1) * 7) + j;
							break;
						case 'R':
							index = ((i + 1) * 7) + j + 82 - i;
							break;
						case 'D':
							index = ((i + 1) * 7) + j + 1;
							break;
						case 'L':
							index = ((i + 1) * 7) + j + 76 - i;
						}
						pathWalls.get(index).setFill(Color.LIMEGREEN);
						pathWalls.get(index).setStroke(Color.AQUA);
						wallOpen[index] = true;
					}
				int I = 0, J = 0;
				for (i = 0; i < 11; ++i)
					for (j = 0; j < 6; ++j)
						if (playerInsideBox[i][j]) {
							I = i;
							J = j;
						}
				for (i = 0; i < 11; ++i)
					for (j = 0; j < 6; ++j)
						playerInsideBox[i][j] = true;
				double X = spaceShip.getCenterX();
				double Y = spaceShip.getCenterY();
				/*
				 * The following algorithm uses a process of elimination to
				 * determine which box the user is currently inside.
				 */
				if (Y <= 150.0)
					for (i = 0; i < 11; ++i)
						for (j = 1; j < 6; ++j)
							playerInsideBox[i][j] = false;
				else if (Y <= 250.0) {
					for (i = 0; i < 11; ++i)
						for (j = 0; j < 6; ++j)
							if (j != 1)
								playerInsideBox[i][j] = false;
				} else if (Y <= 350.0) {
					for (i = 0; i < 11; ++i)
						for (j = 0; j < 6; ++j)
							if (j != 2)
								playerInsideBox[i][j] = false;
				} else if (Y <= 450.0) {
					for (i = 0; i < 11; ++i)
						for (j = 0; j < 6; ++j)
							if (j != 3)
								playerInsideBox[i][j] = false;
				} else if (Y <= 550.0) {
					for (i = 0; i < 11; ++i)
						for (j = 0; j < 6; ++j)
							if (j != 4)
								playerInsideBox[i][j] = false;
				} else if (Y <= 650.0)
					for (i = 0; i < 11; ++i)
						for (j = 0; j < 5; ++j)
							playerInsideBox[i][j] = false;
				if (X <= 150.0)
					for (i = 1; i < 11; ++i)
						for (j = 0; j < 6; ++j)
							playerInsideBox[i][j] = false;
				else if (X <= 250.0) {
					for (i = 0; i < 11; ++i)
						if (i != 1)
							for (j = 0; j < 6; ++j)
								playerInsideBox[i][j] = false;
				} else if (X <= 350.0) {
					for (i = 0; i < 11; ++i)
						if (i != 2)
							for (j = 0; j < 6; ++j)
								playerInsideBox[i][j] = false;
				} else if (X <= 450.0) {
					for (i = 0; i < 11; ++i)
						if (i != 3)
							for (j = 0; j < 6; ++j)
								playerInsideBox[i][j] = false;
				} else if (X <= 550.0) {
					for (i = 0; i < 11; ++i)
						if (i != 4)
							for (j = 0; j < 6; ++j)
								playerInsideBox[i][j] = false;
				} else if (X <= 650.0) {
					for (i = 0; i < 11; ++i)
						if (i != 5)
							for (j = 0; j < 6; ++j)
								playerInsideBox[i][j] = false;
				} else if (X <= 750.0) {
					for (i = 0; i < 11; ++i)
						if (i != 6)
							for (j = 0; j < 6; ++j)
								playerInsideBox[i][j] = false;
				} else if (X <= 850.0) {
					for (i = 0; i < 11; ++i)
						if (i != 7)
							for (j = 0; j < 6; ++j)
								playerInsideBox[i][j] = false;
				} else if (X <= 950.0) {
					for (i = 0; i < 11; ++i)
						if (i != 8)
							for (j = 0; j < 6; ++j)
								playerInsideBox[i][j] = false;
				} else if (X <= 1050.0) {
					for (i = 0; i < 11; ++i)
						if (i != 9)
							for (j = 0; j < 6; ++j)
								playerInsideBox[i][j] = false;
				} else if (X <= 1150.0)
					for (i = 0; i < 10; ++i)
						for (j = 0; j < 6; ++j)
							playerInsideBox[i][j] = false;
				if (!/*
						 * The negation of the player being inside the box they
						 * were before they made this move would be the result
						 * of them moving into a different box.
						 */playerInsideBox[I][J]) {
					try {
						switch (e.getCode()) {
						case U:
						case UP:
							J -= 1;
							switch (doorSwitchCode[I - 1][J]) {
							case 'L':
								doorOpenCode[I - 1][J] = 'L';
								break;
							case 'U':
								doorOpenCode[I - 1][J] = 'U';
								break;
							case 'R':
								doorOpenCode[I - 1][J] = 'R';
							}
							break;
						case R:
						case RIGHT:
							I += 1;
							switch (doorSwitchCode[I - 1][J]) {
							case 'L':
								doorOpenCode[I - 1][J] = 'U';
								break;
							case 'U':
								doorOpenCode[I - 1][J] = 'R';
								break;
							case 'R':
								doorOpenCode[I - 1][J] = 'D';
							}
							break;
						case D:
						case DOWN:
							J += 1;
							switch (doorSwitchCode[I - 1][J]) {
							case 'L':
								doorOpenCode[I - 1][J] = 'R';
								break;
							case 'U':
								doorOpenCode[I - 1][J] = 'D';
								break;
							case 'R':
								doorOpenCode[I - 1][J] = 'L';
							}
							break;
						case L:
						case LEFT:
							I -= 1;
							switch (doorSwitchCode[I - 1][J]) {
							case 'L':
								doorOpenCode[I - 1][J] = 'D';
								break;
							case 'U':
								doorOpenCode[I - 1][J] = 'L';
								break;
							case 'R':
								doorOpenCode[I - 1][J] = 'U';
							}
						}
					} catch (ArrayIndexOutOfBoundsException aioobe) {

					}
				}
				break;
			case A:
				JOptionPane.showMessageDialog(null, "-70 points");
				score -= 70;
				primaryStage.close();
				start(new Stage());
			}
		});
	}

	private static class PathWall extends Rectangle {
		public PathWall(double x, double y, char directionCode) {
			super(x, y, widthAndHeightWithDirectionCode(directionCode)[0],
					widthAndHeightWithDirectionCode(directionCode)[1]);
		}

		private static double[] widthAndHeightWithDirectionCode(char directionCode) {
			double[] widthAndHeightWithDirectionCode = new double[2];
			double width = 5.0, height = 5.0;
			switch (directionCode) {
			case 'u':
				height = -100.0;
				break;
			case 'r':
				width = 100.0;
				break;
			case 'd':
				height = 100.0;
				break;
			case 'l':
				width = -100.0;
			}
			widthAndHeightWithDirectionCode[0] = width;
			widthAndHeightWithDirectionCode[1] = height;
			return widthAndHeightWithDirectionCode;
		}
	}
}
