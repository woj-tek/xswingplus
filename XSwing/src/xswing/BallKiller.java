/*
 * @version 0.0 30.05.2008
 * @author Tobse F
 */
package xswing;

import java.awt.event.*;
import java.util.*;
import lib.mylib.MyTimer;
import lib.mylib.object.*;
import xswing.events.*;
import xswing.events.BallEvent.BallEventType;

public class BallKiller implements Resetable, Updateable, BallEventListener, ActionListener {

	private static final int WAITING_BEFORE_KILL = 320;
	private Mechanics mechanics;
	private HighScoreCounter score;
	private MyTimer timer;
	private List<BallDisbandCollection> disbandCollections = new LinkedList<BallDisbandCollection>();
	private final BallTable ballTable;

	public BallKiller(Mechanics mechanics, HighScoreCounter score, BallTable ballTable) {
		this.mechanics = mechanics;
		this.score = score;
		this.ballTable = ballTable;
		timer = new MyTimer(WAITING_BEFORE_KILL, false, false) {

			@Override
			protected void timerAction() {
			// killBalls();
			}
		};
		reset();
	}

	public void addDisbandCollection(BallDisbandCollection disbandCollection) {
		disbandCollections.add(disbandCollection);
		disbandCollection.addActionListener(this);
	}

	@Override
	public void reset() {
		timer.reset();
	}

	@Override
	public void update(int delta) {
		for (BallDisbandCollection ballSet : disbandCollections) {
			ballSet.update(delta);
		}
	}

	@Override
	public void ballEvent(BallEvent e) {
		if (e.getBallEventType().equals(BallEventType.ADDED_TO_PLAY_FIELD)) {
			System.out.println("Added to balltable");
			 addToDisbandRowIfConnected(e.getBall());
			if (mechanics.isInRowWithThree(e.getBall())) {
				System.out.println("ball with three in a row");
				List<Ball> connectedBalls = mechanics.getConnectedBalls(e.getBall());
				if (connectedBalls.size() >= 3) {
					System.out.println("Found connected balls " + connectedBalls.size() + " " + connectedBalls);
					addDisbandCollection(new BallDisbandCollection(e.getBall(), ballTable, connectedBalls, score,
							mechanics));
				}
			}
		}
	}

	private void addToDisbandRowIfConnected(Ball ball) {
		List<Ball> sorroundings = new LinkedList<Ball>();
		mechanics.getSurroundings(ball, sorroundings);
		for (BallDisbandCollection ballSet : disbandCollections) {
			for (Ball foundBall : sorroundings) {
				if (ballSet.contains(foundBall)) {
					ballSet.add(ball);
					break;
				}
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof BallDisbandCollection) {
			disbandCollections.remove(e.getSource());
		}

	}
}