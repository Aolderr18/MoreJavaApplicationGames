/*
* This event driven program is a game involving 5 projectiles which the user can launch
* when ready.The user must evaluate the paths created by the reflectors and determine
* where each projectile will land.
*/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.Timer;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PathGenerator extends Application {
	static Line topBorder, bottomBorder, leftBorder, rightBorder;
	static Line[] reflectors; // These determine the path of each projectile.
	static Circle[] turningPoints;
	static Circle[] projectiles;
	static Rectangle[] destinations;
	static byte[] projectile_X_Directions, projectile_Y_Directions;
	static int b;
	static int numberOfSelections;
	static char[] pathCodes;
	static char[][] boardCode;
	static char selectionColorCode;
	static char[] destinationBoxColorCodes;
	static boolean inMotion, motionAvailable;
	static PlayerInformation info;
	static boolean infoCreated;

	public void start(Stage primaryStage) {
		ArrayList<Character> pathAssignmentCodes = new ArrayList<Character>();
		pathAssignmentCodes.add('t'); // Top projectile
		pathAssignmentCodes.add('u'); // Upper projectile
		pathAssignmentCodes.add('c'); // Center projectile
		pathAssignmentCodes.add('l'); // Lower projectile
		pathAssignmentCodes.add('b'); // Bottom projectile
		pathCodes = new char[60];
		for (int x = 0; x < pathCodes.length;) {
			int assigner = (int) (Math.random() * pathAssignmentCodes.size());
			char pathCode = pathAssignmentCodes.get(assigner);
			int occurences = 0;
			for (int s = 0; s < pathCodes.length; ++s)
				if (pathCode == pathCodes[s])
					++occurences;
			if (occurences == 12)
				/*
				 * Since there are 5 projectiles and 60 possible vertical
				 * sections for the reflectors they hit, each projectile should
				 * be reflected exactly 12 times.
				 */
				pathAssignmentCodes.remove(assigner);
			else
				pathCodes[x++] = pathCode;
		}
		int[] pathIndex = new int[] { 0, 0, 0, 0, 0 };
		int[] previousRowInPath = new int[] { 0, 0, 0, 0, 0 };
		int[] previousColInPath = new int[] { 0, 0, 0, 0, 0 };
		boolean[] rowAccessible = new boolean[] { false, true, false, true, false, true, false, true, false, true };
		char[] slantType = new char[5];
		for (int d = 0; d < slantType.length; ++d) {
			byte slantAssign = (byte) (Math.random() * 2);
			switch (slantAssign) {
			case 0:
				slantType[d] = 'L';
				break;
			default:
				slantType[d] = 'R';
			}
		}
		reflectors = new Line[60];
		boardCode = new char[60][10];
		for (int h = 0; h < 60; ++h) {
			int row = 0;
			switch (pathCodes[h]) {
			case 't':
				if (pathIndex[0] == 0) {
					boardCode[h][0] = slantType[0];
					previousColInPath[0] = h;
					rowAccessible[0] = true;/*
											 * A row becomes accessible if and
											 * only if the projectile previously
											 * in that row changed its direction
											 * from horizontal to vertical.
											 */
				} else if (pathIndex[0] % 2 == 1) {
					do {
						row = (int) (Math.random() * rowAccessible.length);
					} while (!rowAccessible[row]
							/*
							 * Some rows must be declared inaccessible in order
							 * to ensure each projectile has a clear path to its
							 * destination.
							 */ || boardCode[previousColInPath[0]][row] == slantType[0]);
					boardCode[previousColInPath[0]][row] = slantType[0];
					previousRowInPath[0] = row;
					rowAccessible[row] = false;
					if (slantType[0] == 'L')
						slantType[0] = 'R';
					else
						slantType[0] = 'L';
				} else {
					boardCode[h][previousRowInPath[0]] = slantType[0];
					previousColInPath[0] = h;
					rowAccessible[previousRowInPath[0]] = true;
				}
				++pathIndex[0];
				break;
			case 'u':
				if (pathIndex[1] == 0) {
					boardCode[h][2] = slantType[1];
					previousColInPath[1] = h;
					rowAccessible[2] = true;
				} else if (pathIndex[1] % 2 == 1) {
					do {
						row = (int) (Math.random() * rowAccessible.length);
					} while (!rowAccessible[row] || boardCode[previousColInPath[1]][row] == slantType[1]);
					boardCode[previousColInPath[1]][row] = slantType[1];
					previousRowInPath[1] = row;
					rowAccessible[row] = false;
					if (slantType[1] == 'L')
						slantType[1] = 'R';
					else
						slantType[1] = 'L';
				} else {
					boardCode[h][previousRowInPath[1]] = slantType[1];
					previousColInPath[1] = h;
					rowAccessible[previousRowInPath[1]] = true;
				}
				++pathIndex[1];
				break;
			case 'c':
				if (pathIndex[2] == 0) {
					boardCode[h][4] = slantType[2];
					previousColInPath[2] = h;
					rowAccessible[4] = true;
				} else if (pathIndex[2] % 2 == 1) {
					do {
						row = (int) (Math.random() * rowAccessible.length);
					} while (!rowAccessible[row] || boardCode[previousColInPath[2]][row] == slantType[2]);
					boardCode[previousColInPath[2]][row] = slantType[2];
					previousRowInPath[2] = row;
					rowAccessible[row] = false;
					if (slantType[2] == 'L')
						slantType[2] = 'R';
					else
						slantType[2] = 'L';
				} else {
					boardCode[h][previousRowInPath[2]] = slantType[2];
					previousColInPath[2] = h;
					rowAccessible[previousRowInPath[2]] = true;
				}
				++pathIndex[2];
				break;
			case 'l':
				if (pathIndex[3] == 0) {
					boardCode[h][6] = slantType[3];
					previousColInPath[3] = h;
					rowAccessible[6] = true;
				} else if (pathIndex[3] % 2 == 1) {
					do {
						row = (int) (Math.random() * rowAccessible.length);
					} while (!rowAccessible[row] || boardCode[previousColInPath[3]][row] == slantType[3]);
					boardCode[previousColInPath[3]][row] = slantType[3];
					previousRowInPath[3] = row;
					rowAccessible[row] = false;
					if (slantType[3] == 'L')
						slantType[3] = 'R';
					else
						slantType[3] = 'L';
				} else {
					boardCode[h][previousRowInPath[3]] = slantType[3];
					previousColInPath[3] = h;
					rowAccessible[previousRowInPath[3]] = true;
				}
				++pathIndex[3];
				break;
			case 'b':
				if (pathIndex[4] == 0) {
					boardCode[h][8] = slantType[4];
					previousColInPath[4] = h;
					rowAccessible[8] = true;
				} else if (pathIndex[4] % 2 == 1) {
					do {
						row = (int) (Math.random() * rowAccessible.length);
					} while (!rowAccessible[row] || boardCode[previousColInPath[4]][row] == slantType[4]);
					boardCode[previousColInPath[4]][row] = slantType[4];
					previousRowInPath[4] = row;
					rowAccessible[row] = false;
					if (slantType[4] == 'L')
						slantType[4] = 'R';
					else
						slantType[4] = 'L';
				} else {
					boardCode[h][previousRowInPath[4]] = slantType[4];
					previousColInPath[4] = h;
					rowAccessible[previousRowInPath[4]] = true;
				}
				++pathIndex[4];
			}
		}
		int x = 0;
		int k = 0;
		turningPoints = new Circle[reflectors.length];
		for (int i = 0; i < boardCode.length; ++i)
			for (int j = 0; j < boardCode[i].length; ++j) {
				if (boardCode[i][j] == 'R' || boardCode[i][j] == 'L')
					switch (boardCode[i][j]) {
					case 'R':
						reflectors[x++] = new Line();
						reflectors[x - 1].setStroke(Color.WHITE);
						reflectors[x - 1].setStartX(52 + (16 * i));
						reflectors[x - 1].setEndX(65 + (16 * i));
						reflectors[x - 1].setStartY(76.5 + (65 * j));
						reflectors[x - 1].setEndY(88.5 + (65 * j));
						turningPoints[k++] = new Circle(5);
						turningPoints[k - 1].setCenterX(58.5 + (16 * i));
						turningPoints[k - 1].setCenterY(82.5 + (65 * j));
						break;
					case 'L':
						reflectors[x++] = new Line();
						reflectors[x - 1].setStroke(Color.WHITE);
						reflectors[x - 1].setStartX(52 + (16 * i));
						reflectors[x - 1].setEndX(65 + (16 * i));
						reflectors[x - 1].setStartY(88.5 + (65 * j));
						reflectors[x - 1].setEndY(76.5 + (65 * j));
						turningPoints[k++] = new Circle(5);
						turningPoints[k - 1].setCenterX(58.5 + (16 * i));
						turningPoints[k - 1].setCenterY(82.5 + (65 * j));
					}
			}
		topBorder = new Line(50, 50, 1100, 50);
		bottomBorder = new Line(50, 700, 1100, 700);
		leftBorder = new Line(20, 50, 20, 700);
		rightBorder = new Line(1100, 50, 1100, 700);
		topBorder.setStroke(Color.AQUA);
		bottomBorder.setStroke(Color.AQUA);
		leftBorder.setStroke(Color.SILVER);
		rightBorder.setStroke(Color.AQUA);
		projectiles = new Circle[5];
		for (int d = 0; d < projectiles.length; ++d) {
			projectiles[d] = new Circle(5);
			projectiles[d].setCenterX(20);
			projectiles[d].setCenterY(82.5 + 130 * d);
			switch (d) {
			case 0:
				projectiles[d].setFill(Color.RED);
				break;
			case 1:
				projectiles[d].setFill(Color.YELLOW);
				break;
			case 2:
				projectiles[d].setFill(Color.GREEN);
				break;
			case 3:
				projectiles[d].setFill(Color.PURPLE);
				break;
			case 4:
				projectiles[d].setFill(Color.WHEAT);
			}
		}
		destinations = new Rectangle[10];
		destinations[0] = new Rectangle(1050, 67.5, 50, 30);
		destinations[0].setStroke(Color.WHITE);
		destinations[1] = new Rectangle(1050, 132.5, 50, 30);
		destinations[1].setStroke(Color.WHITE);
		destinations[2] = new Rectangle(1050, 197.5, 50, 30);
		destinations[2].setStroke(Color.WHITE);
		destinations[3] = new Rectangle(1050, 262.5, 50, 30);
		destinations[3].setStroke(Color.WHITE);
		destinations[4] = new Rectangle(1050, 327.5, 50, 30);
		destinations[4].setStroke(Color.WHITE);
		destinations[5] = new Rectangle(1050, 392.5, 50, 30);
		destinations[5].setStroke(Color.WHITE);
		destinations[6] = new Rectangle(1050, 467.5, 50, 30);
		destinations[6].setStroke(Color.WHITE);
		destinations[7] = new Rectangle(1050, 522.5, 50, 30);
		destinations[7].setStroke(Color.WHITE);
		destinations[8] = new Rectangle(1050, 587.5, 50, 30);
		destinations[8].setStroke(Color.WHITE);
		destinations[9] = new Rectangle(1050, 652.5, 50, 30);
		destinations[9].setStroke(Color.WHITE);
		Pane pane = new Pane(topBorder, bottomBorder, leftBorder, rightBorder, destinations[0], destinations[1],
				destinations[2], destinations[3], destinations[4], destinations[5], destinations[6], destinations[7],
				destinations[8], destinations[9], reflectors[0], reflectors[1], reflectors[2], reflectors[3],
				reflectors[4], reflectors[5], reflectors[6], reflectors[7], reflectors[8], reflectors[9],
				reflectors[10], reflectors[11], reflectors[12], reflectors[13], reflectors[14], reflectors[15],
				reflectors[16], reflectors[17], reflectors[18], reflectors[19], reflectors[20], reflectors[21],
				reflectors[22], reflectors[23], reflectors[24], reflectors[25], reflectors[26], reflectors[27],
				reflectors[28], reflectors[29], reflectors[30], reflectors[31], reflectors[32], reflectors[33],
				reflectors[34], reflectors[35], reflectors[36], reflectors[37], reflectors[38], reflectors[39],
				reflectors[40], reflectors[41], reflectors[42], reflectors[43], reflectors[44], reflectors[45],
				reflectors[46], reflectors[47], reflectors[48], reflectors[49], reflectors[50], reflectors[51],
				reflectors[52], reflectors[53], reflectors[54], reflectors[55], reflectors[56], reflectors[57],
				reflectors[58], reflectors[59], projectiles[0], projectiles[1], projectiles[2], projectiles[3],
				projectiles[4]);
		Scene scene = new Scene(pane);
		scene.setFill(Color.BLACK);
		projectile_X_Directions = new byte[5];
		projectile_Y_Directions = new byte[5];
		projectile_X_Directions[0] = 1;
		projectile_X_Directions[1] = 1;
		projectile_X_Directions[2] = 1;
		projectile_X_Directions[3] = 1;
		projectile_X_Directions[4] = 1;
		projectile_Y_Directions[0] = 0;
		projectile_Y_Directions[1] = 0;
		projectile_Y_Directions[2] = 0;
		projectile_Y_Directions[3] = 0;
		projectile_Y_Directions[4] = 0;
		inMotion = false;
		scene.setOnKeyReleased(e -> {
			switch (e.getCode()) {
			case S:
				if (motionAvailable) {
					/**
					 * The round cannot start until each path has been
					 * estimated.
					 */
					inMotion = true;
					TheLaunch launch = new TheLaunch();
					launch.go();
				}
				break;
			case R: // This is the code to restart the round.
				primaryStage.close();
				start(primaryStage);
				break;
			case D:
				int temp = 0;
				for (int d = 0; d < destinationBoxColorCodes.length; ++d) {
					switch (destinationBoxColorCodes[d]) {
					case 'r':
						++temp;
						break;
					case 'y':
						++temp;
						break;
					case 'g':
						++temp;
						break;
					case 'p':
						++temp;
						break;
					case 'w':
						++temp;
					}
				}
				if (temp == 5)
					motionAvailable = true;
				break;
			case C:
				motionAvailable = false;
				for (int w = 0; w < 10; ++w) {
					destinations[w].setFill(Color.BLACK);
					destinationBoxColorCodes[w] = 'n';
				}
			default:
			}
		});
		primaryStage.setScene(scene);
		primaryStage.show();
		JOptionPane.showMessageDialog(null,
				"Please click on each projectile and then click in the destination boxes to predict where they will land.");
		JOptionPane.showMessageDialog(null, "Press the 'd' key when you are done.");
		destinationBoxColorCodes = new char[10];
		for (int n = 0; n < destinationBoxColorCodes.length; ++n)
			destinationBoxColorCodes[n] = 'n';
		scene.setOnMouseReleased(e -> {
			if (!inMotion) {
				Point2D clickPoint = new Point2D(e.getSceneX(), e.getSceneY());
				if (projectiles[0].contains(clickPoint))
					selectionColorCode = 'r';
				else if (projectiles[1].contains(clickPoint))
					selectionColorCode = 'y';
				else if (projectiles[2].contains(clickPoint))
					selectionColorCode = 'g';
				else if (projectiles[3].contains(clickPoint))
					selectionColorCode = 'p';
				else if (projectiles[4].contains(clickPoint))
					selectionColorCode = 'w';
				for (int v = 0; v < destinations.length; ++v) {
					if (destinations[v].contains(clickPoint)) {
						numberOfSelections = 0;
						boolean ableToChange = true;
						if (destinationBoxColorCodes[v] != 'n') {
							destinationBoxColorCodes[v] = 'n';
							destinations[v].setFill(Color.BLACK);
							break;
						}
						for (int q = 0; q < destinationBoxColorCodes.length; ++q)
							if (destinationBoxColorCodes[q] != 'n')
								++numberOfSelections;
						if (numberOfSelections < 5) {
							for (int p = 0; p < destinationBoxColorCodes.length; ++p)
								if (v != p)
									if (destinationBoxColorCodes[p] == selectionColorCode)
										ableToChange = false;
							if (ableToChange) {
								destinationBoxColorCodes[v] = selectionColorCode;
								switch (selectionColorCode) {
								case 'r':
									destinations[v].setFill(Color.RED);
									break;
								case 'y':
									destinations[v].setFill(Color.YELLOW);
									break;
								case 'g':
									destinations[v].setFill(Color.GREEN);
									break;
								case 'p':
									destinations[v].setFill(Color.PURPLE);
									break;
								case 'w':
									destinations[v].setFill(Color.WHEAT);
								}
							} else
								JOptionPane.showMessageDialog(null,
										"Sorry, another destination box is that color already.");
						} else
							JOptionPane.showMessageDialog(null,
									"Sorry, 5 destination boxes have been selected already. Press the 'c' key if you wish to clear them and start over.");
					}
				}
			}
		});
	}

	private class TheLaunch implements ActionListener {
		Timer TM;

		public TheLaunch() {
			TM = new Timer(5, this);
		}

		public void go() {
			TM.start();
		}

		public void end() {
			inMotion = false;
			TM.stop();
			boolean correct = true;
			for (int g = 0; g < 10; ++g)
				switch (destinationBoxColorCodes[g]) {
				case 'r':
					if (!destinations[g]
							.contains(new Point2D(projectiles[0].getCenterX() - 5, projectiles[0].getCenterY())))
						correct = false;
					break;
				case 'y':
					if (!destinations[g]
							.contains(new Point2D(projectiles[1].getCenterX() - 5, projectiles[1].getCenterY())))
						correct = false;
					break;
				case 'g':
					if (!destinations[g]
							.contains(new Point2D(projectiles[2].getCenterX() - 5, projectiles[2].getCenterY())))
						correct = false;
					break;
				case 'p':
					if (!destinations[g]
							.contains(new Point2D(projectiles[3].getCenterX() - 5, projectiles[3].getCenterY())))
						correct = false;
					break;
				case 'w':
					if (!destinations[g]
							.contains(new Point2D(projectiles[4].getCenterX() - 5, projectiles[4].getCenterY())))
						correct = false;
					break;
				}
			if (correct) {
				JOptionPane.showMessageDialog(null, "Congratulations " + info.getPlayerName() + "!"
						+ " You have correctly selected where each projectile will land!");
				int scoreIncrease = info.getNumberOfLives() * 3;
				info.increaseLives();
				info.increaseScore(scoreIncrease);
				JOptionPane.showMessageDialog(null, "+" + scoreIncrease + " points");

			} else {
				info.decreaseLives();
				JOptionPane.showMessageDialog(null, "Sorry " + info.getPlayerName()
						+ " you did not select the correct place where the projectiles will land.");
				JOptionPane.showMessageDialog(null, "You have " + info.getNumberOfLives() + " lives left.");
			}
			motionAvailable = false;
			if (info.getNumberOfLives() == 0 || info.getNumberOfLives() == 25) {
				endGame();
			} else
				JOptionPane.showMessageDialog(null, "Please press the 'R' key to reset the board.");
			for (int l = 0; l < 10; ++l) {
				destinations[l].setFill(Color.BLACK);
				destinationBoxColorCodes[l] = 'n';
			}
			for (int d = 0; d < projectiles.length; ++d) {
				// The projectiles should be reset to their original place.
				projectiles[d].setCenterX(20);
				projectiles[d].setCenterY(82.5 + 130 * d);
				switch (d) {
				case 0:
					projectiles[d].setFill(Color.RED);
					break;
				case 1:
					projectiles[d].setFill(Color.YELLOW);
					break;
				case 2:
					projectiles[d].setFill(Color.GREEN);
					break;
				case 3:
					projectiles[d].setFill(Color.PURPLE);
					break;
				case 4:
					projectiles[d].setFill(Color.WHEAT);
				}
			}
			TM = null;
		}

		private void endGame() {
			if (info.getNumberOfLives() == 0) {
				System.out.println("Sorry " + info.getPlayerName() + ", you are out of lives. Better luck next time!");
				return;
			}
			System.out.println("Congratulations " + info.getPlayerName() + "!" + " You have completely won the game!");
		}

		public void actionPerformed(ActionEvent event) {
			boolean motionExists = false;
			long now = System.currentTimeMillis();
			while (System.currentTimeMillis() - now < 3)
				;
			for (int m = 0; m < projectiles.length; ++m) {
				if (projectile_X_Directions[m] != 0 || projectile_Y_Directions[m] != 0)
					motionExists = true;
				projectiles[m].setCenterX(projectiles[m].getCenterX() + projectile_X_Directions[m]);
				projectiles[m].setCenterY(projectiles[m].getCenterY() + projectile_Y_Directions[m]);
				if (projectiles[m].getCenterY() > 700)
					projectiles[m].setCenterY(51.0);
				if (projectiles[m].getCenterY() < 50)
					projectiles[m].setCenterY(699.0);
				if (projectiles[m].getCenterX() > 1100)
					projectile_X_Directions[m] = 0;
				Point2D centerOfCircle = new Point2D(projectiles[m].getCenterX(), projectiles[m].getCenterY());
				for (int v = 0; v < turningPoints.length; ++v) {
					if (turningPoints[v].contains(centerOfCircle)) {
						switch (boardCode[(int) ((turningPoints[v].getCenterX() - 58.5)
								/ 16.0)][(int) ((turningPoints[v].getCenterY() - 58.5) / 65.0)]) {
						case 'L':
							if (projectile_X_Directions[m] == 0) {
								projectile_X_Directions[m] = 1;
								projectile_Y_Directions[m] = 0;
							} else {
								projectile_X_Directions[m] = 0;
								projectile_Y_Directions[m] = -1;
							}
							break;
						case 'R':
							if (projectile_X_Directions[m] == 0) {
								projectile_X_Directions[m] = 1;
								projectile_Y_Directions[m] = 0;
							} else {
								projectile_X_Directions[m] = 0;
								projectile_Y_Directions[m] = 1;
							}
						}
					}
				}
			}
			if (motionExists) {
				motionExists = false;
				try {
					go();
				} catch (Throwable t) {
					go();
				}
			} else
				end();
		}
	}

	public static void main(String[] args) {
		if (!infoCreated) {
			System.out.println("Please enter your name.");
			Scanner userInput = new Scanner(System.in);
			String name = userInput.nextLine();
			userInput.close();
			info = new PlayerInformation(name);
			infoCreated = true;
		}
		b = 0;
		Application.launch(args);
	}
}

/**
 * The following class is used for creating a single object that is used for
 * storing information about the player.
 */

final class PlayerInformation /*
								 * The final keyword must be used as this class
								 * will never have any reason to be extended.
								 */ {
	private String playerName;
	private int numberOfLives;
	private int score;

	public PlayerInformation(String playerName) {
		this.playerName = playerName;
		numberOfLives = 5;
		score = 0;
	}

	public void increaseLives() {
		++numberOfLives;
	}

	public void decreaseLives() {
		--numberOfLives;
	}

	public void increaseScore(int amount) {
		score += amount;
	}

	public String getPlayerName() {
		String playerName = this.playerName;
		return playerName;
	}

	public int getNumberOfLives() {
		int numberOfLives = this.numberOfLives;
		return numberOfLives;
	}

	public int getScore() {
		int score = this.score;
		return score;
	}
}
