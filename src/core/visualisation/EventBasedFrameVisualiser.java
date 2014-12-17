package core.visualisation;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import core.interfaces.GameInstance;
import core.interfaces.PersistentPlayer;

public class EventBasedFrameVisualiser<S> implements GameInstance {
	
	private GameHandler h;
	private FrameVisualisationHandler<S> v;
	
	private S curState;
	
	private List<VisualGameEvent> curEvents;
	
	private Image backImg;
	private Image stateImg;
	
	private boolean wasVisualising;
	private boolean endGameEventSeen;
	
	public EventBasedFrameVisualiser(GameHandler h, FrameVisualisationHandler<S> v, S initialState) {
		this.h = h;
		this.v = v;
		curState = initialState;
		curEvents = new ArrayList<VisualGameEvent>();
		stateImg = null;
		backImg = null;
		wasVisualising = false;
		endGameEventSeen = false;
	}
	
	@Override
	public void begin() {
		h.begin();
	}

	@Override
	public void getVisualisation(Graphics g, int width, int height) {
		// We first paint the background
		if (backImg == null) {
			handleWindowResize(width, height);
		}
		g.drawImage(backImg, 0, 0, width, height, null);

		// Secondly, paint the state
		if (stateImg == null) {
			redrawState(width, height);
		}
		//g.drawImage(stateImg, 0, 0, width, height, null);

		// Finally, paint the events ontop
		v.generateState(curState, width, height, (Graphics2D) g);
		v.animateEvents(curState, curEvents, width, height, (Graphics2D) g);
		
		// Now fix up events
		List<VisualGameEvent> newEvents = new ArrayList<>();
		boolean stateChanged = false;
		for (VisualGameEvent e : curEvents) {
			e.curFrame++;
			if (e.curFrame == e.totalFrames) {
				v.eventEnded(e, curState);
				stateChanged = true;
			} else {
				newEvents.add(e);
			}
		}
		curEvents = newEvents;
		if (stateChanged) {
			redrawState(width, height);
		}
	}

	@Override
	public void handleWindowResize(int width, int height) {
		BufferedImage newBackImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = newBackImg.createGraphics();
		v.generateBackground(curState, width, height, g);
		g.dispose();
		backImg = newBackImg;
	}
	
	private void redrawState(int width, int height) {
		BufferedImage newStateImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = newStateImg.createGraphics();
		v.generateState(curState, width, height, g);
		g.dispose();
		stateImg = newStateImg;
	}
	
	public void giveEvents(List<VisualGameEvent> events) {
		for (VisualGameEvent e : events) {
			giveEvent(e);
		}
	}
	
	public void giveEvent(VisualGameEvent ev) {
		if (ev instanceof EndGameEvent) {
			endGameEventSeen = true;
			return;
		}
		v.eventCreated(ev);
		ev.curFrame = 0;
		curEvents.add(ev);
	}

	public S getCurState() {
		return curState;
	}
	
	@Override
	public Map<PersistentPlayer, Integer> getResults() {
		return h.getResults();
	}
	
	public boolean finishedVisualising() {
		return curEvents.size() == 0 && endGameEventSeen;
	}

	public boolean isVisualising() {
		return wasVisualising;
	}
}