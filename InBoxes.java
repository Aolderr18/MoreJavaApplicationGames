import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JOptionPane;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class InBoxes extends Application {

	protected static TenByTenGrid[] gameGrids;
	protected static TenByTenGrid[] extraGrids;
	protected static VisibleTenByEightGrid visibleGameGrid;
	static int l, d;
	static int X, Y, Z;
	static int origX, origY, origZ;
	static int N;
	static int currentSelection, previousSelection;
	static short numberOfEvens, numberOfThrees, numberOfFives, numberOfSevens, numberOfFifteens, numberOfPrimes;
	static char viewingDirection, viewingPosition;
	static char previousRotation;
	static boolean rotationMode;
	static boolean[][][] containsNumber;
	static String viewDirectionString, viewPositionString;
	static Integer[][][] numbers;
	static Rectangle[][] boxes;
	static Text XEquals, YEquals, ZEquals;
	static Text evensText, threesText, fivesText, sevensText, fifteensText, primesText;
	static Text selectionText;

	public static void main(String[] args) {
		gameGrids = new TenByTenGrid[20]; /*
											 * This array holds the grids in the
											 * front-back and right-left viewing
											 * directions.
											 */
		for (N = 0; N < gameGrids.length; ++N) {
			short v, w; // These are the coordinates of the destination square.
			v = (short) (Math.random() * 10);
			w = (short) (Math.random() * 10);
			GridPath[] gridPaths = new GridPath[99];
			short i, j;
			int g = 0;
			for (i = 0; i < 10; ++i)
				for (j = 0; j < 10; ++j) {
					if (i == v && j == w)
						/*
						 * It is not necessary for the destination square to
						 * have a grid path to itself.
						 */
						continue;
					int numberOfTurns = (int) (Math.random() * 4) + 3;
					ArrayList<GridVelocity> velocities = new ArrayList<GridVelocity>();
					int x = i, y = j;
					for (int o = 0; o < numberOfTurns; ++o) {
						char direction = ' ';
						switch ((byte) (Math.random() * 4)) {
						case 0:
							direction = 'U';
							break;
						case 1:
							direction = 'D';
							break;
						case 2:
							direction = 'R';
							break;
						case 3:
							direction = 'L';
						}
						short magnitude = 0;
						if (x == v) {
							switch (direction) {
							case 'U':
								if (y - w == 1 || w - y == 9) {
									/*
									 * The magnitude should be set to one if the
									 * square is only one unit away from the
									 * destination.
									 */
									magnitude = 1;
									o = numberOfTurns;
								} else {
									if (y > w)
										do
											magnitude = (short) (Math.random() * 9 + 1);
										/*
										 * The magnitude not cross the
										 * destination square without the
										 * gridpath ending.
										 */
										while (magnitude >= y - w);
									else
										do
											magnitude = (short) (Math.random() * 9 + 1);
										while (magnitude >= 10 - (w - y));
								}
								break;
							case 'D':
								if (w - y == 1 || y - w == 9) {
									magnitude = 1;
									o = numberOfTurns;
								} else {
									if (y > w)
										do
											magnitude = (short) (Math.random() * 9 + 1);
										while (magnitude >= 10 - (y - w));
									else
										do
											magnitude = (short) (Math.random() * 9 + 1);
										while (magnitude >= w - y);
								}
							}
						} else if (y == w) {
							switch (direction) {
							case 'R':
								if (v - x == 1 || x - v == 9) {
									magnitude = 1;
									o = numberOfTurns;
								} else {
									if (x > v)
										do
											magnitude = (short) (Math.random() * 9 + 1);
										while (magnitude >= 10 - (x - v));
									else
										do
											magnitude = (short) (Math.random() * 9 + 1);
										while (magnitude >= v - x);
								}
								break;
							case 'L':
								if (x - v == 1 || v - x == 9) {
									magnitude = 1;
									o = numberOfTurns;
								} else {
									if (x > v)
										do
											magnitude = (short) (Math.random() * 9 + 1);
										while (magnitude >= x - v);
									else
										do
											magnitude = (short) (Math.random() * 9 + 1);
										while (magnitude >= 10 - (v - x));
								}
							}
						} else
							switch (direction) {
							case 'U':
							case 'D':
								magnitude = (short) (Math.random() * 9 + 1);
								break;
							case 'R':
							case 'L':
								magnitude = (short) (Math.random() * 9 + 1);
							}
						velocities.add(new GridVelocity(magnitude, direction));
						switch (direction) {
						case 'U':
							y -= magnitude;
							break;
						case 'D':
							y += magnitude;
							break;
						case 'R':
							x += magnitude;
							break;
						case 'L':
							x -= magnitude;
						}
						x = (x + 10) % 10;
						y = (y + 10) % 10;
					}
					if (y > w)
						velocities.add(new GridVelocity((short) (y - w), 'U'));
					if (y < w)
						velocities.add(new GridVelocity((short) (w - y), 'D'));
					if (x > v)
						velocities.add(new GridVelocity((short) (x - v), 'L'));
					if (x < v)
						velocities.add(new GridVelocity((short) (v - x), 'R'));
					GridVelocity[] velocityVector = new GridVelocity[velocities.size()];
					int h = 0;
					for (GridVelocity vel : velocities)
						velocityVector[h++] = vel;
					try {
						gridPaths[g++] = new GridPath(new TenByTenGridCoordinate(i, j),
								new TenByTenGridCoordinate(v, w), velocityVector);
					} catch (InvalidGridPathException ivgpe) {

					}
				}
			GridVelocity[][] opener_Up = new GridVelocity[10][10];
			GridVelocity[][] opener_Down = new GridVelocity[10][10];
			GridVelocity[][] opener_Right = new GridVelocity[10][10];
			GridVelocity[][] opener_Left = new GridVelocity[10][10];
			for (g = 0; g < gridPaths.length; ++g) {
				int X = gridPaths[g].getOriginalGridCoordinate().getI();
				int Y = gridPaths[g].getOriginalGridCoordinate().getJ();
				for (int m = 0; m < gridPaths[g].getPathVector().length; ++m) {
					short negativePathIndex = (short) (gridPaths[g].getPathVector()[m].getMagnitude() - 1);
					/*
					 * The negativePathIndex variable moves along the start of
					 * the vector to the current position being examined. For
					 * example, if the coordinate being examined is (5, 2) and
					 * the vector is an UP vector from (5, 9), the
					 * negativePathIndex variable would trace from (5, 8), (5,
					 * 7), (5, 6), (5, 5), (5, 4), (5, 3). It would increment
					 * the required velocity to open up each sequential door.
					 */
					switch (gridPaths[g].getPathVector()[m].getDirection()) {
					case 'U':
						Y = (Y + 10 - gridPaths[g].getPathVector()[m].getMagnitude()) % 10;
						if (m < gridPaths[g].getPathVector().length - 1)
							switch (gridPaths[g].getPathVector()[m + 1].getDirection()) {
							case 'U':
								for (; negativePathIndex >= 0; --negativePathIndex)
									try {
										/*
										 * The try - catch blocks here are
										 * essential since if the required
										 * velocity for any particular
										 * coordinate has been assigned already,
										 * it is not necessary to assign it
										 * again unless it contains a larger
										 * velocity than the one needed for the
										 * current path being layed out. If no
										 * velocity has been assigned to a
										 * particular square, a
										 * NullPointerException will be thrown.
										 */
										if (opener_Up[X][(Y + negativePathIndex + 10) % 10]
												.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
											opener_Up[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
									} catch (NullPointerException npe) {
										opener_Up[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
												(short) (gridPaths[g].getPathVector()[m].getMagnitude()
														- negativePathIndex),
												gridPaths[g].getPathVector()[m].getDirection());
									}
								break;
							case 'D':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Down[X][(Y + negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Down[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Down[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Up[X][(Y + negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Up[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											/*
											 * If the coordinate being examed is
											 * the square where the box below
											 * would be open, each square along
											 * the way should open upward. Only
											 * the top square should open
											 * downward.
											 */
											opener_Up[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
								break;
							case 'R':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Right[X][(Y + negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Right[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Right[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Up[X][(Y + negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Up[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Up[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
								break;
							case 'L':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Left[X][(Y + negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Left[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Left[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Up[X][(Y + negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Up[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Up[X][(Y + negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
							}
						break;
					case 'D':
						Y = (Y + 10 + gridPaths[g].getPathVector()[m].getMagnitude()) % 10;
						if (m < gridPaths[g].getPathVector().length - 1)
							switch (gridPaths[g].getPathVector()[m + 1].getDirection()) {
							case 'U':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Up[X][(Y - negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Up[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Up[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Down[X][(Y - negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Down[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Down[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
								break;
							case 'D':
								for (; negativePathIndex >= 0; --negativePathIndex)
									try {
										if (opener_Down[X][(Y - negativePathIndex + 10) % 10]
												.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
											opener_Down[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
									} catch (NullPointerException npe) {
										opener_Down[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
												(short) (gridPaths[g].getPathVector()[m].getMagnitude()
														- negativePathIndex),
												gridPaths[g].getPathVector()[m].getDirection());
									}
								break;
							case 'R':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Right[X][(Y - negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Right[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Right[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Down[X][(Y - negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Down[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Down[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
								break;
							case 'L':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Left[X][(Y - negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Left[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Left[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Down[X][(Y - negativePathIndex + 10) % 10]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Down[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Down[X][(Y - negativePathIndex + 10) % 10] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
							}
						break;
					case 'R':
						X = (X + 10 + gridPaths[g].getPathVector()[m].getMagnitude()) % 10;
						if (m < gridPaths[g].getPathVector().length - 1)
							switch (gridPaths[g].getPathVector()[m + 1].getDirection()) {
							case 'U':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Right[(X - negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Right[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Right[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Up[(X - negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Up[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Up[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
								break;
							case 'D':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Right[(X - negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Right[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Right[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Down[(X - negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Down[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Down[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
								break;
							case 'R':
								for (; negativePathIndex >= 0; --negativePathIndex)
									try {
										if (opener_Right[(X - negativePathIndex + 10) % 10][Y]
												.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
											opener_Right[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
									} catch (NullPointerException npe) {
										opener_Right[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
												(short) (gridPaths[g].getPathVector()[m].getMagnitude()
														- negativePathIndex),
												gridPaths[g].getPathVector()[m].getDirection());
									}
								break;
							case 'L':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Left[(X - negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Left[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Left[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Right[(X - negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Right[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Right[(X - negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
							}
						break;
					case 'L':
						X = (X + 10 - gridPaths[g].getPathVector()[m].getMagnitude()) % 10;
						if (m < gridPaths[g].getPathVector().length - 1)
							switch (gridPaths[g].getPathVector()[m + 1].getDirection()) {
							case 'U':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Left[(X + negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Left[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Left[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Up[(X + negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Up[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Up[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
								break;
							case 'D':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Left[(X + negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Left[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Left[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Down[(X + negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Down[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Down[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
								break;
							case 'R':
								for (; negativePathIndex >= 0; --negativePathIndex)
									if (negativePathIndex == 0)
										try {
											if (opener_Left[(X + negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
												opener_Left[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Left[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
									else
										try {
											if (opener_Right[(X + negativePathIndex + 10) % 10][Y]
													.getMagnitude() > gridPaths[g].getPathVector()[m].getDirection())
												opener_Right[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
														(short) (gridPaths[g].getPathVector()[m].getMagnitude()
																- negativePathIndex),
														gridPaths[g].getPathVector()[m].getDirection());
										} catch (NullPointerException npe) {
											opener_Right[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
										}
								break;
							case 'L':
								for (; negativePathIndex >= 0; --negativePathIndex)
									try {
										if (opener_Left[(X + negativePathIndex + 10) % 10][Y]
												.getMagnitude() > gridPaths[g].getPathVector()[m].getMagnitude())
											opener_Left[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
													(short) (gridPaths[g].getPathVector()[m].getMagnitude()
															- negativePathIndex),
													gridPaths[g].getPathVector()[m].getDirection());
									} catch (NullPointerException npe) {
										opener_Left[(X + negativePathIndex + 10) % 10][Y] = new GridVelocity(
												(short) (gridPaths[g].getPathVector()[m].getMagnitude()
														- negativePathIndex),
												gridPaths[g].getPathVector()[m].getDirection());
									}
							}
					}
				}
			}
			short startI, startJ;
			do
				startI = (short) (Math.random() * 9 + 1);
			while (Math.abs(startI - v) < 4);
			do
				startJ = (short) (Math.random() * 7 + 1);
			while (Math.abs(startJ - w) < 2);
			l = startI;
			d = startJ;
			boolean[][] parameterFiller = new boolean[10][10];
			for (int a = 0; a < parameterFiller.length; ++a)
				for (int b = 0; b < parameterFiller[a].length; ++b)
					parameterFiller[a][b] = false;
			parameterFiller[startI][startJ] = true;
			gameGrids[N] = new TenByTenGrid(new TenByTenGridCoordinate(startI, startJ),
					new GridVelocity((short) 5, 'U'), opener_Up, opener_Down, opener_Right, opener_Left,
					parameterFiller, parameterFiller, parameterFiller, parameterFiller);
		}
		numberOfPrimes = 0;
		numberOfThrees = 0;
		numberOfFives = 0;
		numberOfSevens = 0;
		numberOfFifteens = 0;
		numberOfPrimes = 0;
		containsNumber = new boolean[10][10][10];
		for (X = 0; X < containsNumber.length; ++X)
			for (Y = 0; Y < containsNumber[X].length; ++Y)
				for (Z = 0; Z < containsNumber[X][Y].length; ++Z)
					if ((int) (Math.random()
							* 20) == 1)/*
										 * One in 20 squares should contain a
										 * number.
										 */
						containsNumber[X][Y][Z] = true;
					else
						containsNumber[X][Y][Z] = false;
		numbers = new Integer[10][10][10];
		for (X = 0; X < numbers.length; ++X)
			for (Y = 0; Y < numbers[X].length; ++Y)
				for (Z = 0; Z < numbers[X][Y].length; ++Z) {
					if (!containsNumber[X][Y][Z])/*
													 * There is no reason to
													 * assign a number to a
													 * square which is not
													 * supposed to contain a
													 * number.
													 */
						continue;
					int random;
					do
						random = (int) (Math.random() * 102);
					while (random < 15);
					switch ((int) (Math.random() * 6)) {
					case 1:
						random *= 2;
						break;
					case 2:
						random *= 3;
						break;
					case 3:
						random *= 5;
						break;
					case 4:
						random *= 7;
						break;
					case 5:
						random *= 15;
						break;
					default:
						while (!isPrime(random))
							random = (int) (Math.random() * 102 + 15);
					}
					/*
					 * A number should be exclusively divisible by 2, 3, 5, 7,
					 * 15, or be prime. Note that numbers divisible by 15 are
					 * not counted as being divisible by 3 or 5 in the game.
					 */
					while (random % 6 == 0)
						switch ((int) (Math.random() * 2)) {
						case 0:
							random /= 2;
							break;
						case 1:
							random /= 3;
						}
					while (random % 10 == 0)
						switch ((int) (Math.random() * 2)) {
						case 0:
							random /= 2;
							break;
						case 1:
							random /= 5;
						}
					while (random % 14 == 0)
						switch ((int) (Math.random() * 2)) {
						case 0:
							random /= 2;
							break;
						case 1:
							random /= 7;
						}
					while (random % 30 == 0)
						switch ((int) (Math.random() * 2)) {
						case 0:
							random /= 2;
							break;
						case 1:
							random /= 15;
						}
					while (random % 21 == 0)
						switch ((int) (Math.random() * 2)) {
						case 0:
							random /= 3;
							break;
						case 1:
							random /= 7;
						}
					while (random % 45 == 0)
						switch ((int) (Math.random() * 2)) {
						case 0:
							random /= 3;
							break;
						case 1:
							random /= 15;
						}
					while (random % 35 == 0)
						switch ((int) (Math.random() * 2)) {
						case 0:
							random /= 5;
							break;
						case 1:
							random /= 7;
						}
					while (random % 75 == 0)
						switch ((int) (Math.random() * 2)) {
						case 0:
							random /= 5;
							break;
						case 1:
							random /= 15;
						}
					while (random % 105 == 0)
						switch ((int) (Math.random() * 2)) {
						case 0:
							random /= 7;
							break;
						case 1:
							random /= 15;
						}
					numbers[X][Y][Z] = random;
					if (random < 15)
						--Z;
					else if (random % 2 == 0)
						numberOfEvens++;
					else if (random % 3 == 0 && !(random % 5 == 0))
						numberOfThrees++;
					else if (!(random % 3 == 0) && random % 5 == 0)
						numberOfFives++;
					else if (random % 7 == 0)
						numberOfSevens++;
					else if (random % 15 == 0)
						numberOfFifteens++;
					else
						numberOfPrimes++;
				}
		extraGrids = new TenByTenGrid[10];/*
											 * These grids are used for the
											 * top-bottom viewing directions.
											 */
		boolean[][] parameterFiller = new boolean[10][10];
		for (short a = 0; a < parameterFiller.length; ++a)
			for (short b = 0; b < parameterFiller[a].length; ++b)
				parameterFiller[a][b] = false;
		for (short n = 0; n < extraGrids.length; ++n) {
			GridVelocity[][] opener_Up = new GridVelocity[10][10];
			GridVelocity[][] opener_Down = new GridVelocity[10][10];
			GridVelocity[][] opener_Right = new GridVelocity[10][10];
			GridVelocity[][] opener_Left = new GridVelocity[10][10];
			for (short i = 0; i < 10; ++i)
				for (short j = 0; j < 10; ++j) {
					opener_Up[i][j] = gameGrids[19 - i].getOpener_Right()[9 - j][n];
					opener_Down[i][j] = gameGrids[19 - i].getOpener_Left()[9 - j][n];
					opener_Right[i][j] = gameGrids[9 - i].getOpener_Right()[i][n];
					opener_Left[i][j] = gameGrids[9 - i].getOpener_Left()[i][n];
				}
			extraGrids[n] = new TenByTenGrid(new TenByTenGridCoordinate((short) 0, (short) 0),
					new GridVelocity((short) 5, 'U'), opener_Up, opener_Down, opener_Right, opener_Left,
					parameterFiller, parameterFiller, parameterFiller, parameterFiller);
		}
		N = (int) (Math.random()) * 10;
		try {
			currentSelection = numbers[l][d][N].intValue();
		} catch (NullPointerException npe) {
			currentSelection = 1;
		}
		visibleGameGrid = new VisibleTenByEightGrid(gameGrids[N]);
		rotationMode = false;
		viewingPosition = 'U';
		viewingDirection = 'F';
		visibleGameGrid.gridSet = 'p';
		X = gameGrids[N].getCurrentPosition().getI();
		Y = gameGrids[N].getCurrentPosition().getJ();
		Z = N;
		previousSelection = 1;
		previousRotation = ' ';
		Application.launch(args);
	}

	static boolean isPrime(int num) {
		ArrayList<Integer> listOfPrimes = new ArrayList<Integer>();
		listOfPrimes.add(2);
		int potentialPrime = 2;
		while (potentialPrime++ < num / 2)
			for (Integer prime : listOfPrimes)
				if (!(potentialPrime % prime == 0)) {
					listOfPrimes.add(potentialPrime);
					break;
				}
		for (Integer prime : listOfPrimes)
			if (num % prime == 0)
				return false;
		return true;
	}

	@SuppressWarnings({ "incomplete-switch" })
	public void start(Stage primaryStage) {
		viewPositionString = "Up";
		viewDirectionString = "Front";
		primaryStage.setTitle(
				"Viewing Position = " + viewPositionString + "          Viewing Direction = " + viewDirectionString);
		Pane pane = new Pane();
		Scene scene = new Scene(pane);
		boxes = visibleGameGrid.boxes();
		XEquals = new Text();
		YEquals = new Text();
		ZEquals = new Text();
		XEquals.setText("X = " + X);
		YEquals.setText("Y = " + Y);
		ZEquals.setText("Z = " + Z);
		XEquals.setFill(Color.BLUE);
		YEquals.setFill(Color.GREEN);
		ZEquals.setFill(Color.ORANGE);
		XEquals.setX(1000.0);
		XEquals.setY(200.0);
		YEquals.setX(1000.0);
		YEquals.setY(250.0);
		ZEquals.setX(1000.0);
		ZEquals.setY(300.0);
		pane.getChildren().add(XEquals);
		pane.getChildren().add(YEquals);
		pane.getChildren().add(ZEquals);
		int i, j;
		for (i = 0; i < 10; ++i)
			for (j = 0; j < 10; ++j) {
				boxes[i][j].setFill(Color.TAN);
				pane.getChildren().add(boxes[i][j]);
			}
		evensText = new Text("Even numbers left : " + numberOfEvens);
		threesText = new Text("Numbers divisible by 3 left : " + numberOfThrees);
		fivesText = new Text("Numbers divisible by 5 left : " + numberOfFives);
		sevensText = new Text("Numbers divisible by 7 left : " + numberOfSevens);
		fifteensText = new Text("Numbers divisible by 15 left : " + numberOfFifteens);
		primesText = new Text("Prime numbers left : " + numberOfPrimes);
		evensText.setX(800.0);
		evensText.setY(450.0);
		threesText.setX(800.0);
		threesText.setY(480.0);
		fivesText.setX(800.0);
		fivesText.setY(510.0);
		sevensText.setX(800.0);
		sevensText.setY(540.0);
		fifteensText.setX(800.0);
		fifteensText.setY(570.0);
		primesText.setX(800.0);
		primesText.setY(600.0);
		evensText.setFill(Color.GREENYELLOW);
		threesText.setFill(Color.GREENYELLOW);
		fivesText.setFill(Color.GREENYELLOW);
		sevensText.setFill(Color.GREENYELLOW);
		fifteensText.setFill(Color.GREENYELLOW);
		primesText.setFill(Color.GREENYELLOW);
		pane.getChildren().add(evensText);
		pane.getChildren().add(threesText);
		pane.getChildren().add(fivesText);
		pane.getChildren().add(sevensText);
		pane.getChildren().add(fifteensText);
		pane.getChildren().add(primesText);
		selectionText = new Text("Selection = " + currentSelection);
		selectionText.setFill(Color.TURQUOISE);
		selectionText.setX(840.0);
		selectionText.setY(418.0);
		pane.getChildren().add(selectionText);
		Stack<Rectangle> boxBorders = visibleGameGrid.boxBorders();
		while (!boxBorders.isEmpty())
			pane.getChildren().add(boxBorders.pop());
		scene.setFill(Color.BLACK);
		String viewCycle1 = "FRBL";
		String viewCycle2 = "UFDB";
		String viewCycle3 = "URDL";
		String[] allViewCycles = new String[] { viewCycle1, viewCycle2, viewCycle3 };
		scene.setOnKeyReleased(e -> {
			boolean rotated = false;
			if (e.getCode() == KeyCode.R)
				rotationMode = !rotationMode;
			try {
				if (!rotationMode) {
					origX = X;
					origY = Y;
					origZ = Z;
					switch (e.getCode()) {
					case UP:
						visibleGameGrid.getGrid().move('U');
						switch (viewingPosition) {/*
													 * Depending on the viewing
													 * position, each direction
													 * arrow key should have a
													 * different effect on the
													 * current coordinate.
													 */
						case 'U':
							Y = (Y + 9) % 10;
							break;
						case 'D':
							Y = (Y + 11) % 10;
							break;
						case 'R':
							X = (X + 11) % 10;
							break;
						case 'L':
							X = (X + 9) % 10;
							break;
						case 'F':
							Z = (Z + 11) % 10;
							break;
						case 'B':
							Z = (Z + 9) % 10;
						}
						break;
					case DOWN:
						visibleGameGrid.getGrid().move('D');
						switch (viewingPosition) {
						case 'U':
							Y = (Y + 11) % 10;
							break;
						case 'D':
							Y = (Y + 9) % 10;
							break;
						case 'R':
							X = (X + 9) % 10;
							break;
						case 'L':
							X = (X + 11) % 10;
							break;
						case 'F':
							Z = (Z + 9) % 10;
							break;
						case 'B':
							Z = (Z + 11) % 10;
						}
						break;
					case RIGHT:
						visibleGameGrid.getGrid().move('R');
						switch (viewingPosition) {
						case 'U':
							switch (viewingDirection) {
							case 'F':
								X = (X + 11) % 10;
								break;
							case 'R':
								Z = (Z + 9) % 10;
								break;
							case 'B':
								X = (X + 9) % 10;
								break;
							case 'L':
								Z = (Z + 11) % 10;
							}
							break;
						case 'D':
							switch (viewingDirection) {
							case 'F':
								X = (X + 9) % 10;
								break;
							case 'R':
								Z = (Z + 11) % 10;
								break;
							case 'B':
								X = (X + 11) % 10;
								break;
							case 'L':
								Z = (Z + 9) % 10;
							}
							break;
						case 'R':
							switch (viewingDirection) {
							case 'U':
								Z = (Z + 11) % 10;
								break;
							case 'F':
								Y = (Y + 11) % 10;
								break;
							case 'D':
								Z = (Z + 9) % 10;
								break;
							case 'B':
								Y = (Y + 9) % 10;
							}
							break;
						case 'L':
							switch (viewingDirection) {
							case 'U':
								Z = (Z + 9) % 10;
								break;
							case 'F':
								Y = (Y + 9) % 10;
								break;
							case 'D':
								Z = (Z + 11) % 10;
								break;
							case 'B':
								Y = (Y + 11) % 10;
							}
							break;
						case 'F':
							switch (viewingDirection) {
							case 'U':
								X = (X + 9) % 10;
								break;
							case 'R':
								Y = (Y + 9) % 10;
								break;
							case 'D':
								X = (X + 11) % 10;
								break;
							case 'L':
								Y = (Y + 11) % 10;
							}
							break;
						case 'B':
							switch (viewingDirection) {
							case 'U':
								X = (X + 11) % 10;
								break;
							case 'R':
								Y = (Y + 11) % 10;
								break;
							case 'D':
								X = (X + 9) % 10;
								break;
							case 'L':
								Y = (Y + 9) % 10;
							}
						}
						break;
					case LEFT:
						visibleGameGrid.getGrid().move('L');
						switch (viewingPosition) {
						case 'U':
							switch (viewingDirection) {
							case 'F':
								X = (X + 9) % 10;
								break;
							case 'R':
								Z = (Z + 11) % 10;
								break;
							case 'B':
								X = (X + 11) % 10;
								break;
							case 'L':
								Z = (Z + 9) % 10;
							}
							break;
						case 'D':
							switch (viewingDirection) {
							case 'F':
								X = (X + 11) % 10;
								break;
							case 'R':
								Z = (Z + 11) % 10;
								break;
							case 'B':
								X = (X + 9) % 10;
								break;
							case 'L':
								Z = (Z + 9) % 10;
							}
							break;
						case 'R':
							switch (viewingDirection) {
							case 'U':
								Z = (Z + 9) % 10;
								break;
							case 'F':
								Y = (Y + 9) % 10;
								break;
							case 'D':
								Z = (Z + 11) % 10;
								break;
							case 'B':
								Y = (Y + 11) % 10;
							}
							break;
						case 'L':
							switch (viewingDirection) {
							case 'U':
								Z = (Z + 11) % 10;
								break;
							case 'F':
								Y = (Y + 11) % 10;
								break;
							case 'D':
								Z = (Z + 9) % 10;
								break;
							case 'B':
								Y = (Y + 9) % 10;
							}
							break;
						case 'F':
							switch (viewingDirection) {
							case 'U':
								X = (X + 11) % 10;
								break;
							case 'R':
								Y = (Y + 11) % 10;
								break;
							case 'D':
								X = (X + 9) % 10;
								break;
							case 'L':
								Y = (Y + 9) % 10;
							}
							break;
						case 'B':
							switch (viewingDirection) {
							case 'U':
								X = (X + 9) % 10;
								break;
							case 'R':
								Y = (Y + 9) % 10;
								break;
							case 'D':
								X = (X + 11) % 10;
								break;
							case 'L':
								Y = (Y + 11) % 10;
							}
						}
					}
				} else {
					char temp;
					boolean cycleContainsPos = false, cycleContainsDir = false;
					switch (e.getCode()) {
					case UP:
						if (previousRotation == 'D') {
							JOptionPane.showMessageDialog(null,
									"Sorry, you can't rotate immediately back to where you just were.");
							break;
						}
						if (previousRotation == 'U') {
							JOptionPane.showMessageDialog(null,
									"Sorry, you can't make the same rotation twice in a row.");
							break;
						}
						previousRotation = 'U';
						rotationMode = false;
						rotated = true;
						JOptionPane.showMessageDialog(null, "UP Rotation");
						temp = viewingDirection;
						for (String viewCycle : allViewCycles) {
							char[] viewCycleArray = viewCycle.toCharArray();
							for (char c : viewCycleArray)
								if (c == viewingPosition)
									cycleContainsPos = true;
								else if (c == viewingDirection)
									cycleContainsDir = true;
							if (cycleContainsPos && cycleContainsDir) {
								viewingDirection = viewCycle.charAt((viewCycle.indexOf(viewingDirection) + 1) % 4);
								break;
							} else {
								cycleContainsPos = false;
								cycleContainsDir = false;
							}
						}
						viewingPosition = temp;
						break;
					case DOWN:
						if (previousRotation == 'U') {
							JOptionPane.showMessageDialog(null,
									"Sorry, you can't rotate immediately back to where you just were.");
							break;
						}
						if (previousRotation == 'D') {
							JOptionPane.showMessageDialog(null,
									"Sorry, you can't make the same rotation twice in a row.");
							break;
						}
						previousRotation = 'D';
						rotationMode = false;
						rotated = true;
						JOptionPane.showMessageDialog(null, "DOWN Rotation");
						for (String viewCycle : allViewCycles) {
							char[] viewCycleArray = viewCycle.toCharArray();
							for (char c : viewCycleArray)
								if (c == viewingPosition)
									cycleContainsPos = true;
								else if (c == viewingDirection)
									cycleContainsDir = true;
							if (cycleContainsPos && cycleContainsDir) {
								viewingPosition = viewCycle.charAt((viewCycle.indexOf(viewingDirection) + 2) % 4);
								viewingDirection = viewCycle.charAt((viewCycle.indexOf(viewingDirection) + 3) % 4);
								break;
							} else {
								cycleContainsPos = false;
								cycleContainsDir = false;
							}
						}
						break;
					case RIGHT:
						if (previousRotation == 'L') {
							JOptionPane.showMessageDialog(null,
									"Sorry, you can't rotate immediately back to where you just were.");
							break;
						}
						if (previousRotation == 'R') {
							JOptionPane.showMessageDialog(null,
									"Sorry, you can't make the same rotation twice in a row.");
							break;
						}
						previousRotation = 'R';
						rotationMode = false;
						rotated = true;
						JOptionPane.showMessageDialog(null, "RIGHT Rotation");
						for (String viewCycle : allViewCycles) {
							char[] viewCycleArray = viewCycle.toCharArray();
							for (char c : viewCycleArray)
								if (c == viewingPosition)
									cycleContainsPos = true;
								else if (c == viewingDirection)
									cycleContainsDir = true;
							if (!cycleContainsPos && cycleContainsDir) {
								viewingDirection = viewCycle.charAt((viewCycle.indexOf(viewingDirection) + 3) % 4);
								break;
							} else {
								cycleContainsPos = false;
								cycleContainsDir = false;
							}
						}
						break;
					case LEFT:
						if (previousRotation == 'R') {
							JOptionPane.showMessageDialog(null,
									"Sorry, you can't rotate immediately back to where you just were.");
							break;
						}
						if (previousRotation == 'L') {
							JOptionPane.showMessageDialog(null,
									"Sorry, you can't make the same rotation twice in a row.");
							break;
						}
						previousRotation = 'L';
						rotationMode = false;
						rotated = true;
						JOptionPane.showMessageDialog(null, "LEFT Rotation");
						for (String viewCycle : allViewCycles) {
							char[] viewCycleArray = viewCycle.toCharArray();
							for (char c : viewCycleArray)
								if (c == viewingPosition)
									cycleContainsPos = true;
								else if (c == viewingDirection)
									cycleContainsDir = true;
							if (!cycleContainsPos && cycleContainsDir) {
								viewingDirection = viewCycle.charAt((viewCycle.indexOf(viewingDirection) + 1) % 4);
								break;
							} else {
								cycleContainsPos = false;
								cycleContainsDir = false;
							}
						}
					}
				}
				if (!rotated)
					switch (e.getCode()) {
					case UP:
					case DOWN:
					case RIGHT:
					case LEFT:
						previousSelection = currentSelection;
						if (containsNumber[X][Y][Z])
							currentSelection = numbers[X][Y][Z].intValue();
						selectionText.setText("Selection = " + currentSelection);
						pane.getChildren().clear();
						Stack<Rectangle> updatedBoxBorders = visibleGameGrid.boxBorders();
						while (!updatedBoxBorders.isEmpty())
							pane.getChildren().add(updatedBoxBorders.pop());
						boxes = visibleGameGrid.boxes();
						for (l = 0; l < 10; ++l)
							for (d = 0; d < 10; ++d) {
								/*
								 * The integer variables l and d each represent
								 * the coordinate of the grid as it is viewed
								 * from the screen.
								 */
								switch (visibleGameGrid.gridSet) {
								case 'p':
									switch (viewingPosition) {
									case 'U':
										switch (viewingDirection) {
										case 'F':
											if (containsNumber[l][d][Z])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'B':
											if (containsNumber[9 - l][d][Z])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
										break;
									case 'D':
										switch (viewingDirection) {
										case 'F':
											if (containsNumber[9 - l][9 - d][Z])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'B':
											if (containsNumber[l][9 - d][Z])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
										break;
									case 'R':
										switch (viewingDirection) {
										case 'F':
											if (containsNumber[9 - d][l][Z])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'B':
											if (containsNumber[9 - d][9 - l][Z])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
										break;
									case 'L':
										switch (viewingDirection) {
										case 'F':
											if (containsNumber[d][9 - l][Z])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'B':
											if (containsNumber[d][l][Z])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
									}
									break;
								case 's':
									switch (viewingPosition) {
									case 'U':
										switch (viewingDirection) {
										case 'R':
											if (containsNumber[X][d][9 - l])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'L':
											if (containsNumber[X][d][l])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
										break;
									case 'D':
										switch (viewingDirection) {
										case 'R':
											if (containsNumber[X][9 - d][l])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'L':
											if (containsNumber[X][9 - d][9 - l])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
										break;
									case 'F':
										switch (viewingDirection) {
										case 'R':
											if (containsNumber[X][9 - l][9 - d])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'L':
											if (containsNumber[X][l][9 - d])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
										break;
									case 'B':
										switch (viewingDirection) {
										case 'R':
											if (containsNumber[X][l][d])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'L':
											if (containsNumber[X][9 - l][d])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
									}
									break;
								case 't':
									switch (viewingPosition) {
									case 'F':
										switch (viewingDirection) {
										case 'U':
											if (containsNumber[9 - l][Y][9 - d])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'D':
											if (containsNumber[l][Y][9 - d])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
										break;
									case 'B':
										switch (viewingDirection) {
										case 'U':
											if (containsNumber[l][Y][d])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'D':
											if (containsNumber[9 - l][Y][d])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
										break;
									case 'R':
										switch (viewingDirection) {
										case 'U':
											if (containsNumber[9 - d][Y][l])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'D':
											if (containsNumber[9 - d][Y][9 - l])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
										break;
									case 'L':
										switch (viewingDirection) {
										case 'U':
											if (containsNumber[d][Y][9 - l])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
											break;
										case 'D':
											if (containsNumber[d][Y][l])
												boxes[l][d].setFill(Color.LAWNGREEN);
											else
												boxes[l][d].setFill(Color.WHEAT);
										}
									}
								}
								if (visibleGameGrid.getGrid().getCurrentPosition().getI() == l
										&& visibleGameGrid.getGrid().getCurrentPosition().getJ() == d)
									boxes[l][d].setFill(Color.SILVER);
								pane.getChildren().add(boxes[l][d]);
							}
						pane.getChildren().add(selectionText);
						if (!(previousSelection == currentSelection) && !(previousSelection == 1))
							if (previousSelection % 2 == 0 && currentSelection % 3 == 0
									&& !(currentSelection % 5 == 0)) {
								JOptionPane.showMessageDialog(null,
										"Great job! You eliminated " + numbers[X][Y][Z].toString() + "!");
								numbers[X][Y][Z] = null;
								containsNumber[X][Y][Z] = false;
								--numberOfThrees;
								previousSelection = currentSelection;
							} else if (previousSelection % 3 == 0 && currentSelection % 5 == 0
									&& !(currentSelection % 3 == 0) && !(previousSelection % 5 == 0)) {
								JOptionPane.showMessageDialog(null,
										"Great job! You eliminated " + numbers[X][Y][Z].toString() + "!");
								numbers[X][Y][Z] = null;
								containsNumber[X][Y][Z] = false;
								--numberOfFives;
								previousSelection = currentSelection;
							} else if (previousSelection % 5 == 0 && currentSelection % 7 == 0
									&& !(previousSelection % 3 == 0)) {
								JOptionPane.showMessageDialog(null,
										"Great job! You eliminated " + numbers[X][Y][Z].toString() + "!");
								numbers[X][Y][Z] = null;
								containsNumber[X][Y][Z] = false;
								--numberOfSevens;
								previousSelection = currentSelection;
							} else if (previousSelection % 7 == 0 && currentSelection % 15 == 0) {
								JOptionPane.showMessageDialog(null,
										"Great job! You eliminated " + numbers[X][Y][Z].toString() + "!");
								numbers[X][Y][Z] = null;
								containsNumber[X][Y][Z] = false;
								--numberOfFifteens;
								previousSelection = currentSelection;
							} else if (previousSelection % 15 == 0 && isPrime(currentSelection)) {
								JOptionPane.showMessageDialog(null,
										"Great job! You eliminated " + numbers[X][Y][Z].toString() + "!");
								numbers[X][Y][Z] = null;
								containsNumber[X][Y][Z] = false;
								--numberOfPrimes;
								previousSelection = currentSelection;
							} else if (isPrime(previousSelection) && currentSelection % 2 == 0) {
								JOptionPane.showMessageDialog(null,
										"Great job! You eliminated " + numbers[X][Y][Z].toString() + "!");
								numbers[X][Y][Z] = null;
								containsNumber[X][Y][Z] = false;
								--numberOfEvens;
								previousSelection = currentSelection;
							}

						evensText.setText("Even numbers left : " + numberOfEvens);
						threesText.setText("Numbers divisible by 3 left : " + numberOfThrees);
						fivesText.setText("Numbers divisible by 5 left : " + numberOfFives);
						sevensText.setText("Numbers divisible by 7 left : " + numberOfSevens);
						fifteensText.setText("Numbers divisible by 15 left : " + numberOfFifteens);
						primesText.setText("Prime numbers left : " + numberOfPrimes);
						XEquals.setText("X = " + X);
						YEquals.setText("Y = " + Y);
						ZEquals.setText("Z = " + Z);
						XEquals.setFill(Color.BLUE);
						YEquals.setFill(Color.GREEN);
						ZEquals.setFill(Color.ORANGE);
						XEquals.setX(1000.0);
						XEquals.setY(200.0);
						YEquals.setX(1000.0);
						YEquals.setY(250.0);
						ZEquals.setX(1000.0);
						ZEquals.setY(300.0);
						pane.getChildren().add(XEquals);
						pane.getChildren().add(YEquals);
						pane.getChildren().add(ZEquals);
						pane.getChildren().add(evensText);
						pane.getChildren().add(threesText);
						pane.getChildren().add(fivesText);
						pane.getChildren().add(sevensText);
						pane.getChildren().add(fifteensText);
						pane.getChildren().add(primesText);
					}
				else {
					pane.getChildren().clear();
					switch (viewingDirection) {
					case 'U':
						switch (viewingPosition) {
						case 'R':
							visibleGameGrid.update(extraGrids[Y].flip());
							visibleGameGrid.getGrid().overrideCurrentPosition(Z, 9 - X);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) Z,
									(short) (9 - X));
							break;
						case 'L':
							visibleGameGrid.update(extraGrids[Y].flip().inverse());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - Z, X);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - Z),
									(short) X);
							break;
						case 'F':
							visibleGameGrid.update(extraGrids[Y].flip().leftRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - X, 9 - Z);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - X),
									(short) (9 - Z));
							break;
						case 'B':
							visibleGameGrid.update(extraGrids[Y].flip().rightRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(X, Z);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) X,
									(short) Z);
						}
						break;
					case 'D':
						switch (viewingPosition) {
						case 'R':
							visibleGameGrid.update(extraGrids[Y].inverse());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - Z, 9 - X);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - Z),
									(short) (9 - X));
							break;
						case 'L':
							visibleGameGrid.update(extraGrids[Y]);
							visibleGameGrid.getGrid().overrideCurrentPosition(Z, X);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) Z,
									(short) X);
							break;
						case 'F':
							visibleGameGrid.update(extraGrids[Y].leftRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(X, 9 - Z);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) X,
									(short) (9 - Z));
							break;
						case 'B':
							visibleGameGrid.update(extraGrids[Y].rightRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - X, Z);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - X),
									(short) Z);
						}
						break;
					case 'R':
						switch (viewingPosition) {
						case 'U':
							visibleGameGrid.update(gameGrids[19 - X].horizontalInverse());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - Z, Y);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - Z),
									(short) Y);
							break;
						case 'D':
							visibleGameGrid.update(gameGrids[19 - X].horizontalInverse().inverse());
							visibleGameGrid.getGrid().overrideCurrentPosition(Z, 9 - Y);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) Z,
									(short) (9 - Y));
							break;
						case 'F':
							visibleGameGrid.update(gameGrids[19 - X].horizontalInverse().rightRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - Y, 9 - Z);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - Y),
									(short) (9 - Z));
							break;
						case 'B':
							visibleGameGrid.update(gameGrids[19 - X].horizontalInverse().leftRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(Y, Z);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) Y,
									(short) Z);
						}
						break;
					case 'L':
						switch (viewingPosition) {
						case 'U':
							visibleGameGrid.update(gameGrids[19 - X]);
							visibleGameGrid.getGrid().overrideCurrentPosition(Z, Y);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) Z,
									(short) Y);
							break;
						case 'D':
							visibleGameGrid.update(gameGrids[19 - X].inverse());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - Z, 9 - Y);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - Z),
									(short) (9 - Y));
							break;
						case 'F':
							visibleGameGrid.update(gameGrids[19 - X].leftRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(Y, 9 - Z);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) Y,
									(short) (9 - Z));
							break;
						case 'B':
							visibleGameGrid.update(gameGrids[19 - X].rightRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - Y, Z);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - Y),
									(short) Z);
						}
						break;
					case 'F':
						switch (viewingPosition) {
						case 'U':
							visibleGameGrid.update(gameGrids[Z]);
							visibleGameGrid.getGrid().overrideCurrentPosition(X, Y);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) X,
									(short) Y);
							break;
						case 'D':
							visibleGameGrid.update(gameGrids[Z].inverse());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - X, 9 - Y);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - X),
									(short) (9 - Y));
							break;
						case 'R':
							visibleGameGrid.update(gameGrids[Z].leftRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(Y, 9 - X);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) Y,
									(short) (9 - X));
							break;
						case 'L':
							visibleGameGrid.update(gameGrids[Z].rightRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - Y, X);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - Y),
									(short) X);
						}
						break;
					case 'B':
						switch (viewingPosition) {
						case 'U':
							visibleGameGrid.update(gameGrids[Z].horizontalInverse());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - X, Y);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - X),
									(short) Y);
							break;
						case 'D':
							visibleGameGrid.update(gameGrids[Z].horizontalInverse().inverse());
							visibleGameGrid.getGrid().overrideCurrentPosition(X, 9 - Y);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) X,
									(short) (9 - Y));
							break;
						case 'R':
							visibleGameGrid.update(gameGrids[Z].horizontalInverse().rightRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(9 - Y, 9 - X);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) (9 - Y),
									(short) (9 - X));
							break;
						case 'L':
							visibleGameGrid.update(gameGrids[Z].horizontalInverse().leftRotation());
							visibleGameGrid.getGrid().overrideCurrentPosition(Y, X);
							visibleGameGrid.getGrid().positionOfEntrance = new TenByTenGridCoordinate((short) Y,
									(short) X);
						}
					}
					switch (viewingDirection) {
					case 'U':
					case 'D':
						visibleGameGrid.gridSet = 't';
						break;
					case 'R':
					case 'L':
						visibleGameGrid.gridSet = 's';
						break;
					case 'F':
					case 'B':
						visibleGameGrid.gridSet = 'p';
					}
					Stack<Rectangle> updatedBoxBorders = visibleGameGrid.boxBorders();
					while (!updatedBoxBorders.isEmpty())
						pane.getChildren().add(updatedBoxBorders.pop());
					boxes = visibleGameGrid.boxes();
					for (l = 0; l < 10; ++l)
						for (d = 0; d < 10; ++d) {
							switch (visibleGameGrid.gridSet) {
							case 'p':
								switch (viewingPosition) {
								case 'U':
									switch (viewingDirection) {
									case 'F':
										if (containsNumber[l][d][Z])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'B':
										if (containsNumber[9 - l][d][Z])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
									break;
								case 'D':
									switch (viewingDirection) {
									case 'F':
										if (containsNumber[9 - l][9 - d][Z])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'B':
										if (containsNumber[l][9 - d][Z])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
									break;
								case 'R':
									switch (viewingDirection) {
									case 'F':
										if (containsNumber[9 - d][l][Z])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'B':
										if (containsNumber[9 - d][9 - l][Z])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
									break;
								case 'L':
									switch (viewingDirection) {
									case 'F':
										if (containsNumber[d][9 - l][Z])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'B':
										if (containsNumber[d][l][Z])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
								}
								break;
							case 's':
								switch (viewingPosition) {
								case 'U':
									switch (viewingDirection) {
									case 'R':
										if (containsNumber[X][d][9 - l])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'L':
										if (containsNumber[X][d][l])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
									break;
								case 'D':
									switch (viewingDirection) {
									case 'R':
										if (containsNumber[X][9 - d][l])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'L':
										if (containsNumber[X][9 - d][9 - l])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
									break;
								case 'F':
									switch (viewingDirection) {
									case 'R':
										if (containsNumber[X][9 - l][9 - d])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'L':
										if (containsNumber[X][l][9 - d])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
									break;
								case 'B':
									switch (viewingDirection) {
									case 'R':
										if (containsNumber[X][l][d])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'L':
										if (containsNumber[X][9 - l][d])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
								}
								break;
							case 't':
								switch (viewingPosition) {
								case 'F':
									switch (viewingDirection) {
									case 'U':
										if (containsNumber[9 - l][Y][9 - d])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'D':
										if (containsNumber[l][Y][9 - d])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
									break;
								case 'B':
									switch (viewingDirection) {
									case 'U':
										if (containsNumber[l][Y][d])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'D':
										if (containsNumber[9 - l][Y][d])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
									break;
								case 'R':
									switch (viewingDirection) {
									case 'U':
										if (containsNumber[9 - d][Y][l])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'D':
										if (containsNumber[9 - d][Y][9 - l])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
									break;
								case 'L':
									switch (viewingDirection) {
									case 'U':
										if (containsNumber[d][Y][9 - l])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
										break;
									case 'D':
										if (containsNumber[d][Y][l])
											boxes[l][d].setFill(Color.LAWNGREEN);
										else
											boxes[l][d].setFill(Color.WHEAT);
									}
								}
							}
							if (visibleGameGrid.getGrid().getCurrentPosition().getI() == l
									&& visibleGameGrid.getGrid().getCurrentPosition().getJ() == d)
								boxes[l][d].setFill(Color.SILVER);
							pane.getChildren().add(boxes[l][d]);
						}
					XEquals.setText("X = " + X);
					YEquals.setText("Y = " + Y);
					ZEquals.setText("Z = " + Z);
					XEquals.setFill(Color.BLUE);
					YEquals.setFill(Color.GREEN);
					ZEquals.setFill(Color.ORANGE);
					XEquals.setX(1000.0);
					XEquals.setY(200.0);
					YEquals.setX(1000.0);
					YEquals.setY(250.0);
					ZEquals.setX(1000.0);
					ZEquals.setY(300.0);
					pane.getChildren().add(XEquals);
					pane.getChildren().add(YEquals);
					pane.getChildren().add(ZEquals);
					pane.getChildren().add(evensText);
					pane.getChildren().add(threesText);
					pane.getChildren().add(fivesText);
					pane.getChildren().add(sevensText);
					pane.getChildren().add(fifteensText);
					pane.getChildren().add(primesText);
					pane.getChildren().add(selectionText);
				}
			} catch (ImpossibleGridMoveException ipgme) {
				JOptionPane.showMessageDialog(null, "That move is not possible.");
			}
			switch (viewingPosition) {
			case 'U':
				viewPositionString = "Up";
				break;
			case 'D':
				viewPositionString = "Down";
				break;
			case 'R':
				viewPositionString = "Right";
				break;
			case 'L':
				viewPositionString = "Left";
				break;
			case 'F':
				viewPositionString = "Front";
				break;
			case 'B':
				viewPositionString = "Back";
			}
			switch (viewingDirection) {
			case 'U':
				viewDirectionString = "Up";
				break;
			case 'D':
				viewDirectionString = "Down";
				break;
			case 'R':
				viewDirectionString = "Right";
				break;
			case 'L':
				viewDirectionString = "Left";
				break;
			case 'F':
				viewDirectionString = "Front";
				break;
			case 'B':
				viewDirectionString = "Back";
			}
			primaryStage.setTitle("Viewing Position = " + viewPositionString + "          Viewing Direction = "
					+ viewDirectionString);
			switch (e.getCode()) {
			case UP:
			case DOWN:
			case RIGHT:
			case LEFT:
				short[] remainingNumbers = new short[] { numberOfEvens, numberOfThrees, numberOfFives, numberOfSevens,
						numberOfFifteens, numberOfPrimes };
				short totalRemainingNumbers = 0;
				for (int s = 0; s < remainingNumbers.length * 2; ++s)
					if (s < remainingNumbers.length) {
						if (remainingNumbers[s] > 0 && remainingNumbers[(s + 5) % 6] == 0
								&& remainingNumbers[(s + 1) % 6] == 0) {
							JOptionPane.showMessageDialog(null, "Sorry, you have lost the game.");
							primaryStage.close();
						}
					} else
						totalRemainingNumbers += remainingNumbers[s - 6];
				if (totalRemainingNumbers == 0) {
					JOptionPane.showMessageDialog(null, "Congratulations! You have won the game!");
					primaryStage.close();
				}
			}
		});
		scene.setOnMouseClicked(
				e -> {/*
						 * The user should be able to find out what number is
						 * stored in a particular box by clicking on it.
						 */
					for (l = 0; l < 10; ++l)
						for (d = 0; d < 10; ++d)
							if (boxes[l][d].contains(new Point2D(e.getSceneX(), e.getSceneY()))) {
								try {
									switch (viewingPosition) {
									case 'U':
										switch (viewingDirection) {
										case 'F':
											JOptionPane.showMessageDialog(null, numbers[l][d][Z].toString());
											break;
										case 'R':
											JOptionPane.showMessageDialog(null, numbers[X][d][9 - l].toString());
											break;
										case 'B':
											JOptionPane.showMessageDialog(null, numbers[9 - l][d][Z].toString());
											break;
										case 'L':
											JOptionPane.showMessageDialog(null, numbers[X][d][l].toString());
											break;
										}
										break;
									case 'D':
										switch (viewingDirection) {
										case 'F':
											JOptionPane.showMessageDialog(null, numbers[9 - l][9 - d][Z].toString());
											break;
										case 'R':
											JOptionPane.showMessageDialog(null, numbers[X][9 - d][l].toString());
											break;
										case 'B':
											JOptionPane.showMessageDialog(null, numbers[l][9 - d][Z].toString());
											break;
										case 'L':
											JOptionPane.showMessageDialog(null, numbers[X][9 - d][9 - l].toString());
											break;
										}
										break;
									case 'R':
										switch (viewingDirection) {
										case 'U':
											JOptionPane.showMessageDialog(null, numbers[9 - d][Y][l].toString());
											break;
										case 'F':
											JOptionPane.showMessageDialog(null, numbers[9 - d][l][Z].toString());
											break;
										case 'D':
											JOptionPane.showMessageDialog(null, numbers[9 - d][Y][9 - l].toString());
											break;
										case 'B':
											JOptionPane.showMessageDialog(null, numbers[9 - d][9 - l][Z].toString());
											break;
										}
										break;
									case 'L':
										switch (viewingDirection) {
										case 'U':
											JOptionPane.showMessageDialog(null, numbers[d][Y][9 - l].toString());
											break;
										case 'F':
											JOptionPane.showMessageDialog(null, numbers[d][9 - l][Z].toString());
											break;
										case 'D':
											JOptionPane.showMessageDialog(null, numbers[d][Y][l].toString());
											break;
										case 'B':
											JOptionPane.showMessageDialog(null, numbers[d][l][Z].toString());
											break;
										}
										break;
									case 'F':
										switch (viewingDirection) {
										case 'U':
											JOptionPane.showMessageDialog(null, numbers[9 - l][Y][9 - d].toString());
											break;
										case 'R':
											JOptionPane.showMessageDialog(null, numbers[X][9 - l][9 - d].toString());
											break;
										case 'D':
											JOptionPane.showMessageDialog(null, numbers[l][Y][9 - d].toString());
											break;
										case 'L':
											JOptionPane.showMessageDialog(null, numbers[X][l][9 - d].toString());
											break;
										}
										break;
									case 'B':
										switch (viewingDirection) {
										case 'U':
											JOptionPane.showMessageDialog(null, numbers[l][Y][d].toString());
											break;
										case 'R':
											JOptionPane.showMessageDialog(null, numbers[X][l][d].toString());
											break;
										case 'D':
											JOptionPane.showMessageDialog(null, numbers[9 - l][Y][d].toString());
											break;
										case 'L':
											JOptionPane.showMessageDialog(null, numbers[X][9 - l][d].toString());
										}
									}
								} catch (NullPointerException npe) {

								}
							}
				});
		primaryStage.setScene(scene);
		primaryStage.show();
	}
}

class VisibleTenByEightGrid {
	private TenByTenGrid grid;
	public char gridSet;

	public VisibleTenByEightGrid(TenByTenGrid grid) {
		setGrid(grid);
	}

	void update(TenByTenGrid other) {
		setGrid(other);
	}

	Stack<Rectangle> boxBorders() {
		Stack<Rectangle> reverseBoxBorders = new Stack<Rectangle>();
		Rectangle[] doors = doors();
		Rectangle[] walls = walls();
		short i, j;
		for (short r = 0; r < 400; ++r) {
			i = (short) ((r / 10) % 10);
			j = (short) (r % 10);
			if (r < 100) {
				if (grid.isOpenAbove()[i][j])
					reverseBoxBorders.push(doors[r]);
				else
					reverseBoxBorders.push(walls[r]);
			} else if (r < 200) {
				if (grid.isOpenBelow()[i][j])
					reverseBoxBorders.push(doors[r]);
				else
					reverseBoxBorders.push(walls[r]);
			} else if (r < 300) {
				if (grid.isOpenLeft()[i][j])
					reverseBoxBorders.push(doors[r]);
				else
					reverseBoxBorders.push(walls[r]);
			} else {
				if (grid.isOpenRight()[i][j])
					reverseBoxBorders.push(doors[r]);
				else
					reverseBoxBorders.push(walls[r]);
			}
		}
		Stack<Rectangle> boxBorders = new Stack<Rectangle>();
		while (!reverseBoxBorders.isEmpty())
			boxBorders.push(reverseBoxBorders.pop());
		return boxBorders;
	}

	Rectangle[][] boxes() {
		Rectangle[][] boxes = new Rectangle[10][10];
		short i, j;
		for (i = 0; i < boxes.length; ++i)
			for (j = 0; j < boxes[i].length; ++j) {
				boxes[i][j] = new Rectangle();
				boxes[i][j].setWidth(60.0);
				boxes[i][j].setHeight(60.0);
				boxes[i][j].setX(50.0 + (70.0 * i));
				boxes[i][j].setY(7.5 + (70.0 * j));
				if (grid.getCurrentPosition().getI() == i && grid.getCurrentPosition().getJ() == j)
					boxes[i][j].setFill(Color.SILVER);
				else
					boxes[i][j].setFill(Color.WHITE);
			}
		return boxes;
	}

	Rectangle[] doors() {
		Rectangle[] doors = new Rectangle[400];
		Rectangle[][] boxes = boxes();
		short i, j;
		for (short d = 0; d < doors.length; ++d) {
			i = (short) ((d / 10) % 10);
			j = (short) (d % 10);
			doors[d] = new Rectangle();
			if (d < 100) {
				doors[d].setX(boxes[i][j].getX());
				doors[d].setY(boxes[i][j].getY() - 5.0);
				doors[d].setWidth(60.0);
				doors[d].setHeight(5.0);
			} else if (d < 200) {
				doors[d].setX(boxes[i][j].getX());
				doors[d].setY(boxes[i][j].getY() + 60.0);
				doors[d].setWidth(60.0);
				doors[d].setHeight(5.0);
			} else if (d < 300) {
				doors[d].setX(boxes[i][j].getX() - 5.0);
				doors[d].setY(boxes[i][j].getY());
				doors[d].setWidth(5.0);
				doors[d].setHeight(60.0);
			} else {
				doors[d].setX(boxes[i][j].getX() + 60.0);
				doors[d].setY(boxes[i][j].getY());
				doors[d].setWidth(5.0);
				doors[d].setHeight(60.0);
			}
			doors[d].setFill(Color.AQUAMARINE);
			doors[d].setStroke(Color.ROYALBLUE);
		}
		return doors;
	}

	Rectangle[] walls() {
		Rectangle[] walls = new Rectangle[400];
		Rectangle[][] boxes = boxes();
		short i, j;
		for (short d = 0; d < walls.length; ++d) {
			i = (short) ((d / 10) % 10);
			j = (short) (d % 10);
			i %= 10;
			walls[d] = new Rectangle();
			if (d < 100) {
				walls[d].setX(boxes[i][j].getX());
				walls[d].setY(boxes[i][j].getY() - 5.0);
				walls[d].setWidth(60.0);
				walls[d].setHeight(5.0);
			} else if (d < 200) {
				walls[d].setX(boxes[i][j].getX());
				walls[d].setY(boxes[i][j].getY() + 60.0);
				walls[d].setWidth(60.0);
				walls[d].setHeight(5.0);
			} else if (d < 300) {
				walls[d].setX(boxes[i][j].getX() - 5.0);
				walls[d].setY(boxes[i][j].getY());
				walls[d].setWidth(5.0);
				walls[d].setHeight(60.0);
			} else {
				walls[d].setX(boxes[i][j].getX() + 60.0);
				walls[d].setY(boxes[i][j].getY());
				walls[d].setWidth(5.0);
				walls[d].setHeight(60.0);
			}
			walls[d].setFill(Color.RED);
			walls[d].setStroke(Color.ORANGE);
		}
		return walls;
	}

	public TenByTenGrid getGrid() {
		TenByTenGrid grid = this.grid;
		return grid;
	}

	public void setGrid(TenByTenGrid grid) {
		this.grid = grid;
	}
}

class GridPath {
	private TenByTenGridCoordinate originalGridCoordinate;
	private TenByTenGridCoordinate destinationGridCoordinate;
	private GridVelocity[] pathVector;

	public GridPath(TenByTenGridCoordinate originalGridCoordinate, TenByTenGridCoordinate destinationGridCoordinate,
			GridVelocity[] pathVector) throws InvalidGridPathException {
		short i = originalGridCoordinate.getI();
		short j = originalGridCoordinate.getJ();
		for (int p = 0; p < pathVector.length; ++p)
			switch (pathVector[p].getDirection()) {
			case 'U':
				j = (short) ((j + 10) - pathVector[p].getMagnitude());
				break;
			case 'D':
				j = (short) ((j + 10) + pathVector[p].getMagnitude());
				break;
			case 'R':
				i = (short) ((i + 10) + pathVector[p].getMagnitude());
				break;
			case 'L':
				i = (short) ((i + 10) - pathVector[p].getMagnitude());
			}
		if (i % 10 != destinationGridCoordinate.getI() || j % 10 != destinationGridCoordinate.getJ())
			throw new InvalidGridPathException();
		setOriginalGridCoordinate(originalGridCoordinate);
		setDestinationGridCoordinate(destinationGridCoordinate);
		setPathVector(pathVector);
	}

	public TenByTenGridCoordinate getOriginalGridCoordinate() {
		TenByTenGridCoordinate originalGridCoordinate = this.originalGridCoordinate;
		return originalGridCoordinate;
	}

	public TenByTenGridCoordinate getDestinationGridCoordinate() {
		TenByTenGridCoordinate destinationGridCoordinate = this.destinationGridCoordinate;
		return destinationGridCoordinate;
	}

	public GridVelocity[] getPathVector() {
		GridVelocity[] pathVector = new GridVelocity[this.pathVector.length];
		pathVector = this.pathVector.clone();
		return pathVector;
	}

	private void setDestinationGridCoordinate(TenByTenGridCoordinate destinationGridCoordinate) {
		this.destinationGridCoordinate = destinationGridCoordinate;
	}

	private void setPathVector(GridVelocity[] pathVector) {
		this.pathVector = new GridVelocity[pathVector.length];
		this.pathVector = pathVector.clone();
	}

	private void setOriginalGridCoordinate(TenByTenGridCoordinate originalGridCoordinate) {
		this.originalGridCoordinate = originalGridCoordinate;
	}
}

class InvalidGridPathException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidGridPathException() {

	}
}

class TenByTenGrid {
	private TenByTenGridCoordinate currentPosition;
	private GridVelocity currentVelocity;
	private GridVelocity[][] opener_Up;
	private GridVelocity[][] opener_Down;
	private GridVelocity[][] opener_Right;
	private GridVelocity[][] opener_Left;
	private boolean[][] openAbove;
	private boolean[][] openBelow;
	private boolean[][] openRight;
	private boolean[][] openLeft;
	public TenByTenGridCoordinate positionOfEntrance;

	public TenByTenGrid(TenByTenGridCoordinate currentPosition, GridVelocity currentVelocity,
			GridVelocity[][] opener_Up, GridVelocity[][] opener_Down, GridVelocity[][] opener_Right,
			GridVelocity[][] opener_Left, boolean[][] openAbove, boolean[][] openBelow, boolean[][] openRight,
			boolean[][] openLeft) {
		setCurrentPosition(currentPosition);
		setCurrentVelocity(currentVelocity);
		setOpener_Up(opener_Up);
		setOpener_Down(opener_Down);
		setOpener_Right(opener_Right);
		setOpener_Left(opener_Left);
		setOpenAbove(openAbove);
		setOpenBelow(openBelow);
		setOpenRight(openRight);
		setOpenLeft(openLeft);
		positionOfEntrance = new TenByTenGridCoordinate(currentPosition.getI(), currentPosition.getJ());
		/*
		 * Since the velocity magnitude will be equal to zero at the beginning,
		 * the borders around the square should automatically be open.
		 */
		for (int I = 0; I < 10; ++I)
			for (int J = 0; J < 10; ++J) {
				openAbove[I][J] = false;
				openBelow[I][J] = false;
				openRight[I][J] = false;
				openLeft[I][J] = false;
			}
	}

	/*
	 * The following method returns an instance of the current grid object after
	 * being rotated to the right.
	 */
	public TenByTenGrid rightRotation() {
		GridVelocity[][] opener_Up = new GridVelocity[10][10];
		GridVelocity[][] opener_Down = new GridVelocity[10][10];
		GridVelocity[][] opener_Right = new GridVelocity[10][10];
		GridVelocity[][] opener_Left = new GridVelocity[10][10];
		boolean[][] openAbove = new boolean[10][10];
		boolean[][] openBelow = new boolean[10][10];
		boolean[][] openRight = new boolean[10][10];
		boolean[][] openLeft = new boolean[10][10];
		char newDirection = 0;
		switch (currentVelocity.getDirection()) {
		case 'U':
			newDirection = 'R';
			break;
		case 'D':
			newDirection = 'L';
			break;
		case 'R':
			newDirection = 'D';
			break;
		case 'L':
			newDirection = 'U';
		}
		for (short i = 0; i < 10; ++i)
			for (short j = 0; j < 10; ++j) {
				opener_Up[9 - j][i] = this.opener_Up[i][j];
				opener_Down[9 - j][i] = this.opener_Down[i][j];
				opener_Right[9 - j][i] = this.opener_Right[i][j];
				opener_Left[9 - j][i] = this.opener_Left[i][j];
				openAbove[9 - j][i] = this.openAbove[i][j];
				openBelow[9 - j][i] = this.openBelow[i][j];
				openRight[9 - j][i] = this.openRight[i][j];
				openLeft[9 - j][i] = this.openLeft[i][j];
			}
		return new TenByTenGrid(
				new TenByTenGridCoordinate((short) (9 - currentPosition.getJ()), currentPosition.getI()),
				new GridVelocity(currentVelocity.getMagnitude(), newDirection), opener_Up, opener_Down, opener_Right,
				opener_Left, openAbove, openBelow, openRight, openLeft);
	}

	/*
	 * The following method returns an instance of the current grid object after
	 * being rotated to the left.
	 */
	public TenByTenGrid leftRotation() {
		GridVelocity[][] opener_Up = new GridVelocity[10][10];
		GridVelocity[][] opener_Down = new GridVelocity[10][10];
		GridVelocity[][] opener_Right = new GridVelocity[10][10];
		GridVelocity[][] opener_Left = new GridVelocity[10][10];
		boolean[][] openAbove = new boolean[10][10];
		boolean[][] openBelow = new boolean[10][10];
		boolean[][] openRight = new boolean[10][10];
		boolean[][] openLeft = new boolean[10][10];
		char newDirection = 0;
		switch (currentVelocity.getDirection()) {
		case 'U':
			newDirection = 'R';
			break;
		case 'D':
			newDirection = 'L';
			break;
		case 'R':
			newDirection = 'D';
			break;
		case 'L':
			newDirection = 'U';
		}
		for (short i = 0; i < 10; ++i)
			for (short j = 0; j < 10; ++j) {
				opener_Up[j][9 - i] = this.opener_Up[i][j];
				opener_Down[j][9 - i] = this.opener_Down[i][j];
				opener_Right[j][9 - i] = this.opener_Right[i][j];
				opener_Left[j][9 - i] = this.opener_Left[i][j];
				openAbove[j][9 - i] = this.openAbove[i][j];
				openBelow[j][9 - i] = this.openBelow[i][j];
				openRight[j][9 - i] = this.openRight[i][j];
				openLeft[j][9 - i] = this.openLeft[i][j];
			}
		return new TenByTenGrid(
				new TenByTenGridCoordinate((short) (9 - currentPosition.getJ()), currentPosition.getI()),
				new GridVelocity(currentVelocity.getMagnitude(), newDirection), opener_Up, opener_Down, opener_Right,
				opener_Left, openAbove, openBelow, openRight, openLeft);
	}

	/*
	 * The following method returns an instance of the current grid object after
	 * being vertically inverted.
	 */
	public TenByTenGrid flip() {
		GridVelocity[][] opener_Up = new GridVelocity[10][10];
		GridVelocity[][] opener_Down = new GridVelocity[10][10];
		GridVelocity[][] opener_Right = new GridVelocity[10][10];
		GridVelocity[][] opener_Left = new GridVelocity[10][10];
		boolean[][] openAbove = new boolean[10][10];
		boolean[][] openBelow = new boolean[10][10];
		boolean[][] openRight = new boolean[10][10];
		boolean[][] openLeft = new boolean[10][10];
		char newDirection = 0;
		switch (currentVelocity.getDirection()) {
		case 'U':
			newDirection = 'R';
			break;
		case 'D':
			newDirection = 'L';
			break;
		case 'R':
			newDirection = 'D';
			break;
		case 'L':
			newDirection = 'U';
		}
		for (short i = 0; i < 10; ++i)
			for (short j = 0; j < 10; ++j) {
				opener_Up[i][9 - j] = this.opener_Up[i][j];
				opener_Down[i][9 - j] = this.opener_Down[i][j];
				opener_Right[i][9 - j] = this.opener_Right[i][j];
				opener_Left[i][9 - j] = this.opener_Left[i][j];
				openAbove[i][9 - j] = this.openAbove[i][j];
				openBelow[i][9 - j] = this.openBelow[i][j];
				openRight[i][9 - j] = this.openRight[i][j];
				openLeft[i][9 - j] = this.openLeft[i][j];
			}
		return new TenByTenGrid(
				new TenByTenGridCoordinate((short) (9 - currentPosition.getJ()), currentPosition.getI()),
				new GridVelocity(currentVelocity.getMagnitude(), newDirection), opener_Up, opener_Down, opener_Right,
				opener_Left, openAbove, openBelow, openRight, openLeft);
	}

	/*
	 * The following method returns an instance of the current grid object after
	 * being both vertically and horizontally inverted.
	 */
	public TenByTenGrid inverse() {
		GridVelocity[][] opener_Up = new GridVelocity[10][10];
		GridVelocity[][] opener_Down = new GridVelocity[10][10];
		GridVelocity[][] opener_Right = new GridVelocity[10][10];
		GridVelocity[][] opener_Left = new GridVelocity[10][10];
		boolean[][] openAbove = new boolean[10][10];
		boolean[][] openBelow = new boolean[10][10];
		boolean[][] openRight = new boolean[10][10];
		boolean[][] openLeft = new boolean[10][10];
		char newDirection = 0;
		switch (currentVelocity.getDirection()) {
		case 'U':
			newDirection = 'D';
			break;
		case 'D':
			newDirection = 'U';
			break;
		case 'R':
			newDirection = 'L';
			break;
		case 'L':
			newDirection = 'R';
		}
		for (short i = 0; i < 10; ++i)
			for (short j = 0; j < 10; ++j) {
				opener_Up[9 - i][9 - j] = this.opener_Up[i][j];
				opener_Down[9 - i][9 - j] = this.opener_Down[i][j];
				opener_Right[9 - i][9 - j] = this.opener_Right[i][j];
				opener_Left[9 - i][9 - j] = this.opener_Left[i][j];
				openAbove[9 - i][9 - j] = this.openAbove[i][j];
				openBelow[9 - i][9 - j] = this.openBelow[i][j];
				openRight[9 - i][9 - j] = this.openRight[i][j];
				openLeft[9 - i][9 - j] = this.openLeft[i][j];
			}
		return new TenByTenGrid(
				new TenByTenGridCoordinate((short) (9 - currentPosition.getJ()), currentPosition.getI()),
				new GridVelocity(currentVelocity.getMagnitude(), newDirection), opener_Up, opener_Down, opener_Right,
				opener_Left, openAbove, openBelow, openRight, openLeft);
	}

	/*
	 * The following method returns an instance of the current grid object after
	 * being horizonally inverted.
	 */

	public TenByTenGrid horizontalInverse() {
		GridVelocity[][] opener_Up = new GridVelocity[10][10];
		GridVelocity[][] opener_Down = new GridVelocity[10][10];
		GridVelocity[][] opener_Right = new GridVelocity[10][10];
		GridVelocity[][] opener_Left = new GridVelocity[10][10];
		boolean[][] openAbove = new boolean[10][10];
		boolean[][] openBelow = new boolean[10][10];
		boolean[][] openRight = new boolean[10][10];
		boolean[][] openLeft = new boolean[10][10];
		char newDirection = 0;
		switch (currentVelocity.getDirection()) {
		case 'U':
			newDirection = 'D';
			break;
		case 'D':
			newDirection = 'U';
			break;
		case 'R':
			newDirection = 'L';
			break;
		case 'L':
			newDirection = 'R';
		}
		for (short i = 0; i < 10; ++i)
			for (short j = 0; j < 10; ++j) {
				opener_Up[9 - i][j] = this.opener_Up[i][j];
				opener_Down[9 - i][j] = this.opener_Down[i][j];
				opener_Right[9 - i][j] = this.opener_Right[i][j];
				opener_Left[9 - i][j] = this.opener_Left[i][j];
				openAbove[9 - i][j] = this.openAbove[i][j];
				openBelow[9 - i][j] = this.openBelow[i][j];
				openRight[9 - i][j] = this.openRight[i][j];
				openLeft[9 - i][j] = this.openLeft[i][j];
			}
		return new TenByTenGrid(
				new TenByTenGridCoordinate((short) (9 - currentPosition.getJ()), currentPosition.getI()),
				new GridVelocity(currentVelocity.getMagnitude(), newDirection), opener_Up, opener_Down, opener_Right,
				opener_Left, openAbove, openBelow, openRight, openLeft);
	}

	void overrideCurrentPosition(int i, int j) {
		currentPosition = new TenByTenGridCoordinate((short) i, (short) j);
		positionOfEntrance = currentPosition;
		for (i = 0; i < 10; ++i)
			for (j = 0; j < 10; ++j) {
				openAbove[i][j] = false;
				openBelow[i][j] = false;
				openRight[i][j] = false;
				openLeft[i][j] = false;
			}
		openAbove[positionOfEntrance.getI()][positionOfEntrance.getJ()] = true;
		openBelow[positionOfEntrance.getI()][positionOfEntrance.getJ()] = true;
		openRight[positionOfEntrance.getI()][positionOfEntrance.getJ()] = true;
		openLeft[positionOfEntrance.getI()][positionOfEntrance.getJ()] = true;
	}

	void move(char direction) throws ImpossibleGridMoveException {
		switch (direction) {
		case 'U':
			if (!openAbove[currentPosition.getI()][currentPosition.getJ()])
				/*
				 * The square must be open in the direction trying to be moved
				 * in.
				 */
				throw new ImpossibleGridMoveException("Cannot move upward");
			if (currentVelocity.getDirection() == direction)
				currentVelocity.increaseMagnitude((short) 1);
			else
				currentVelocity = new GridVelocity((short) 1, 'U');
			break;
		case 'D':
			if (!openBelow[currentPosition.getI()][currentPosition.getJ()])
				throw new ImpossibleGridMoveException("Cannot move downward");
			if (currentVelocity.getDirection() == direction)
				currentVelocity.increaseMagnitude((short) 1);
			else
				currentVelocity = new GridVelocity((short) 1, 'D');
			break;
		case 'R':
			if (!openRight[currentPosition.getI()][currentPosition.getJ()])
				throw new ImpossibleGridMoveException("Cannot move right");
			if (currentVelocity.getDirection() == direction)
				currentVelocity.increaseMagnitude((short) 1);
			else
				currentVelocity = new GridVelocity((short) 1, 'R');
			break;
		case 'L':
			if (!openLeft[currentPosition.getI()][currentPosition.getJ()])
				throw new ImpossibleGridMoveException("Cannot move left");
			if (currentVelocity.getDirection() == direction)
				currentVelocity.increaseMagnitude((short) 1);
			else
				currentVelocity = new GridVelocity((short) 1, 'L');
			break;
		default:
			throw new ImpossibleGridMoveException("Unknown direction code");
		}
		currentPosition.move(direction);
		Integer oa_i = 10, oa_j = 10, ob_i = 10, ob_j = 10, or_i = 10, or_j = 10, ol_i = 10, ol_j = 10;
		try {
			if (currentVelocity.containsSubVelocity(opener_Up[currentPosition.getI()][currentPosition.getJ()])) {
				oa_i = new Integer(currentPosition.getI());
				oa_j = new Integer(currentPosition.getJ());
			}
		} catch (NullPointerException npe) {

		}
		try {
			if (currentVelocity.containsSubVelocity(opener_Down[currentPosition.getI()][currentPosition.getJ()])) {
				ob_i = new Integer(currentPosition.getI());
				ob_j = new Integer(currentPosition.getJ());
			}
		} catch (NullPointerException npe) {

		}
		try {
			if (currentVelocity.containsSubVelocity(opener_Right[currentPosition.getI()][currentPosition.getJ()])) {
				or_i = new Integer(currentPosition.getI());
				or_j = new Integer(currentPosition.getJ());
			}
		} catch (NullPointerException npe) {

		}
		try {
			if (currentVelocity.containsSubVelocity(opener_Left[currentPosition.getI()][currentPosition.getJ()])) {
				ol_i = new Integer(currentPosition.getI());
				ol_j = new Integer(currentPosition.getJ());
			}
		} catch (NullPointerException npe) {

		}
		int f, z;
		for (f = 0; f < 10; ++f)
			for (z = 0; z < 10; ++z) {
				if (f == oa_i && z == oa_j)
					openAbove[oa_i][oa_j] = true;
				if (f == ob_i && z == ob_j)
					openBelow[ob_i][ob_j] = true;
				if (f == or_i && z == or_j)
					openRight[or_i][or_j] = true;
				if (f == ol_i && z == ol_j)
					openLeft[ol_i][ol_j] = true;
			}
		switch (currentVelocity.getDirection()) {
		case 'U':
			ob_i = new Integer(currentPosition.getI());
			ob_j = new Integer(currentPosition.getJ());
			break;
		case 'D':
			oa_i = new Integer(currentPosition.getI());
			oa_j = new Integer(currentPosition.getJ());
			break;
		case 'R':
			ol_i = new Integer(currentPosition.getI());
			ol_j = new Integer(currentPosition.getJ());
			break;
		case 'L':
			or_i = new Integer(currentPosition.getI());
			or_j = new Integer(currentPosition.getJ());
		}
		for (f = 0; f < 10; ++f)
			for (z = 0; z < 10; ++z) {
				if (f == oa_i && z == oa_j)
					openAbove[oa_i][oa_j] = true;
				if (f == ob_i && z == ob_j)
					openBelow[ob_i][ob_j] = true;
				if (f == or_i && z == or_j)
					openRight[or_i][or_j] = true;
				if (f == ol_i && z == ol_j)
					openLeft[ol_i][ol_j] = true;
			}
		openAbove[positionOfEntrance.getI()][positionOfEntrance.getJ()] = true;
		openBelow[positionOfEntrance.getI()][positionOfEntrance.getJ()] = true;
		openRight[positionOfEntrance.getI()][positionOfEntrance.getJ()] = true;
		openLeft[positionOfEntrance.getI()][positionOfEntrance.getJ()] = true;
	}

	public TenByTenGridCoordinate getCurrentPosition() {
		TenByTenGridCoordinate currentPosition = this.currentPosition;
		return currentPosition;
	}

	public GridVelocity getCurrentVelocity() {
		GridVelocity currentVelocity = this.currentVelocity;
		return currentVelocity;
	}

	public GridVelocity[][] getOpener_Up() {
		GridVelocity[][] opener_Up = new GridVelocity[this.opener_Up.length][this.opener_Up[0].length];
		opener_Up = this.opener_Up.clone();
		return opener_Up;
	}

	public GridVelocity[][] getOpener_Down() {
		GridVelocity[][] opener_Down = new GridVelocity[this.opener_Down.length][this.opener_Down[0].length];
		opener_Down = this.opener_Down.clone();
		return opener_Down;
	}

	public GridVelocity[][] getOpener_Right() {
		GridVelocity[][] opener_Right = new GridVelocity[this.opener_Right.length][this.opener_Right[0].length];
		opener_Right = this.opener_Right.clone();
		return opener_Right;
	}

	public GridVelocity[][] getOpener_Left() {
		GridVelocity[][] opener_Left = new GridVelocity[this.opener_Left.length][this.opener_Left[0].length];
		opener_Left = this.opener_Left.clone();
		return opener_Left;
	}

	public boolean[][] isOpenAbove() {
		boolean[][] openAbove = new boolean[this.openAbove.length][this.openAbove[0].length];
		openAbove = this.openAbove.clone();
		return openAbove;
	}

	public boolean[][] isOpenBelow() {
		boolean[][] openBelow = new boolean[this.openBelow.length][this.openBelow[0].length];
		openBelow = this.openBelow.clone();
		return openBelow;
	}

	public boolean[][] isOpenRight() {
		boolean[][] openRight = new boolean[this.openRight.length][this.openRight[0].length];
		openRight = this.openRight.clone();
		return openRight;
	}

	public boolean[][] isOpenLeft() {
		boolean[][] openLeft = new boolean[this.openLeft.length][this.openLeft[0].length];
		openLeft = this.openLeft.clone();
		return openLeft;
	}

	private void setCurrentPosition(TenByTenGridCoordinate currentPosition) {
		this.currentPosition = currentPosition;
	}

	private void setCurrentVelocity(GridVelocity currentVelocity) {
		this.currentVelocity = currentVelocity;
	}

	private void setOpener_Up(GridVelocity[][] opener_Up) {
		this.opener_Up = new GridVelocity[opener_Up.length][opener_Up[0].length];
		this.opener_Up = opener_Up.clone();
	}

	private void setOpener_Down(GridVelocity[][] opener_Down) {
		this.opener_Down = new GridVelocity[opener_Down.length][opener_Down[0].length];
		this.opener_Down = opener_Down.clone();
	}

	private void setOpener_Right(GridVelocity[][] opener_Right) {
		this.opener_Right = new GridVelocity[opener_Right.length][opener_Right[0].length];
		this.opener_Right = opener_Right.clone();
	}

	private void setOpener_Left(GridVelocity[][] opener_Left) {
		this.opener_Left = new GridVelocity[opener_Left.length][opener_Left[0].length];
		this.opener_Left = opener_Left.clone();
	}

	private void setOpenAbove(boolean[][] openAbove) {
		this.openAbove = new boolean[openAbove.length][openAbove[0].length];
		for (int i = 0; i < openAbove.length; ++i)
			for (int j = 0; j < openAbove[i].length; ++j)
				this.openAbove[i][j] = openAbove[i][j];
	}

	private void setOpenBelow(boolean[][] openBelow) {
		this.openBelow = new boolean[openBelow.length][openBelow[0].length];
		for (int i = 0; i < openBelow.length; ++i)
			for (int j = 0; j < openBelow[i].length; ++j)
				this.openBelow[i][j] = openBelow[i][j];
	}

	private void setOpenRight(boolean[][] openRight) {
		this.openRight = new boolean[openRight.length][openRight[0].length];
		for (int i = 0; i < openRight.length; ++i)
			for (int j = 0; j < openRight[i].length; ++j)
				this.openRight[i][j] = openRight[i][j];
	}

	private void setOpenLeft(boolean[][] openLeft) {
		this.openLeft = new boolean[openLeft.length][openLeft[0].length];
		for (int i = 0; i < openLeft.length; ++i)
			for (int j = 0; j < openLeft[i].length; ++j)
				this.openLeft[i][j] = openLeft[i][j];
	}
}

class ImpossibleGridMoveException extends Exception {
	private static final long serialVersionUID = 1L;

	public ImpossibleGridMoveException(String reason) {
		super(reason);
	}
}

class TenByTenGridCoordinate {
	private short i;
	private short j;

	public TenByTenGridCoordinate(short i, short j) {
		i %= 10;
		j %= 10;
		setI(i);
		setJ(j);
	}

	@Override
	public String toString() {
		return "(" + i + ", " + j + ")";
	}

	void move(char direction) {
		switch (direction) {
		case 'U':
			j -= 1;
			break;
		case 'D':
			j += 1;
			break;
		case 'R':
			i += 1;
			break;
		case 'L':
			i -= 1;
		}
		i = (short) ((i + 10) % 10);
		j = (short) ((j + 10) % 10);
	}

	public short getI() {
		short i = this.i;
		return i;
	}

	public short getJ() {
		short j = this.j;
		return j;
	}

	private void setI(short i) {
		this.i = i;
	}

	private void setJ(short j) {
		this.j = j;
	}
}

class GridVelocity {
	private short magnitude;
	private char direction;

	public GridVelocity(short magnitude, char direction) {
		setMagnitude(magnitude);
		setDirection(direction);
	}

	@Override
	public String toString() {
		return magnitude + " " + direction;
	}

	void increaseMagnitude(short amount) {
		magnitude += amount;
	}

	void decreaseMagnitude(short amount) {
		magnitude -= amount;
	}

	void changeDirection(char newDirection) {
		direction = newDirection;
	}

	boolean containsSubVelocity(GridVelocity other) {
		return other.getDirection() == direction && other.getMagnitude() <= magnitude;
	}

	public short getMagnitude() {
		short magnitude = this.magnitude;
		return magnitude;
	}

	public char getDirection() {
		char direction = this.direction;
		return direction;
	}

	private void setMagnitude(short magnitude) {
		this.magnitude = magnitude;
	}

	private void setDirection(char direction) {
		this.direction = direction;
	}
}
