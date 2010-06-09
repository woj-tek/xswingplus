/*
 * @version 0.0 04.06.2010
 * @author Tobse F
 */
package xswing;

import java.awt.Point;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.EventListenerList;
import lib.mylib.MyTimer;
import lib.mylib.object.Updateable;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.pathfinding.*;
import xswing.events.BallEvent.BallEventType;

public class BallDisbandCollection implements Updateable {

	private final Ball initator;
	private final Point initatorPos;
	private List<BallWithDistance> balls = new LinkedList<BallWithDistance>();
	private HashSet<Ball> ballsIncluded = new HashSet<Ball>();
	/** Time before killing starts */
	private MyTimer timeBeforeKill;
	/** Time steps of killing all balls with the same distance */
	private MyTimer timeDuringKill;
	private static final int WAITING_BEFORE_KILL = 800;
	private static final int WAITING_BEFORE_NEXT_STEP = 225;
	private final BallTable ballTable;
	private PathFinder pathFinder = null;
	private EventListenerList listenerList = new EventListenerList();
	private HighScoreCounter score;
	private Mechanics mechanics;
	private boolean killingStarted = false;

	public BallDisbandCollection(Ball initator, BallTable ballTable, Collection<Ball> connectedBalls,
			HighScoreCounter score, Mechanics mechanics) {
		this.initator = initator;
		this.ballTable = ballTable;
		this.score = score;
		this.mechanics = mechanics;
		pathFinder = new AStarPathFinder(ballTable, (BallTable.LINES - 1) * 2, false);

		timeBeforeKill = new MyTimer(WAITING_BEFORE_KILL, false, true) {

			@Override
			protected void timerAction() {
				kill();
			}
		};

		initatorPos = ballTable.getField(initator);

		timeDuringKill = new MyTimer(WAITING_BEFORE_NEXT_STEP, true, false) {

			@Override
			protected void timerAction() {
				if (!balls.isEmpty()) {
					killAllBallsWithSameDistance();
				} else {
					timeDuringKill.stop();
					notifyListener(new ActionEvent(this, 0, "Killed All Balls"));
				}
			}
		};
		addAll(connectedBalls);
	}

	public void addActionListener(ActionListener listener) {
		listenerList.add(ActionListener.class, listener);
	}

	public void kill() {
		killingStarted = true;
		Collections.sort(balls);
		LinkedList<Ball> balls2 = new LinkedList<Ball>();
		for (BallWithDistance field : balls) {
			balls2.add(field.getBall());
		}
		score.score(mechanics.calculateScore(balls2));
		balls.get(0).getBall().fireBallEvent(BallEventType.BALL_EXPLODED);
		timeDuringKill.start();
	}

	private void killAllBallsWithSameDistance() {
		System.out.println("killing()");
		int distance = balls.get(0).getDistance();
		while (!balls.isEmpty() && balls.get(0).getDistance() == distance) {
			// balls.get(0).getBall().fireBallEvent(BallEventType.BALL_EXPLODED);
			balls.get(0).getBall().fireBallEvent(BallEventType.BALL_CAUGHT_BY_EXPLOSION);
			balls.remove(0);
		}
	}

	public void add(Ball ball) {
		if (!killingStarted) {
			if (ball == null) {
				throw new IllegalArgumentException("Ball can't be null");
			}
			balls.add(new BallWithDistance(ball));
			ballsIncluded.add(ball);
			timeBeforeKill.reset();
		}
	}

	public void addAll(Collection<Ball> balls) {
		for (Ball ball : balls) {
			add(ball);
		}
	}

	boolean contains(Ball ball) {
		return ballsIncluded.contains(ball);
	}

	private class BallWithDistance implements Comparable<BallWithDistance> {

		private final Ball ball;
		private final int distanceToInitator;

		public BallWithDistance(Ball ball) {
			this.ball = ball;
			if (ball.equals(initator)) {
				distanceToInitator = 0;
			} else {
				Point posBall = ballTable.getField(ball);
				Path pathBall = pathFinder.findPath(initator, initatorPos.x, initatorPos.y, posBall.x, posBall.y);
				if (pathBall == null) {
					Log.error("Ball in BallDisbandCollection (" + ball + ") on position " + posBall
							+ " is not connected with intial ball");
					distanceToInitator = Integer.MAX_VALUE;
				} else {
					distanceToInitator = pathBall.getLength();
				}
			}
		}

		@Override
		public int compareTo(BallWithDistance ball) {
			return distanceToInitator - ball.distanceToInitator;
		}

		public Ball getBall() {
			return ball;
		}

		@Override
		public String toString() {
			return String.valueOf(distanceToInitator);
		}

		public int getDistance() {
			return distanceToInitator;
		}
	}

	private void notifyListener(ActionEvent event) {
		for (ActionListener listener : listenerList.getListeners(ActionListener.class)) {
			listener.actionPerformed(event);
		}
	}

	@Override
	public void update(int delta) {
		timeBeforeKill.update(delta);
		timeDuringKill.update(delta);
	}
}
