package com.shatteredpixel.shatteredpixeldungeon.context;

public class SessionManager {

	private static DungeonSession activeSession;

	public static void init() {
		if (activeSession == null) {
			activeSession = new DungeonSession();
		}
		syncFromDungeon();
	}

	public static DungeonSession getSession() {
		if (activeSession == null) {
			activeSession = new DungeonSession();
			syncFromDungeon();
		}
		return activeSession;
	}

	public static void setSession(DungeonSession session) {
		activeSession = session;
		if (session != null) {
			session.syncToDungeon();
		}
	}

	public static void syncFromDungeon() {
		if (activeSession != null) {
			activeSession.syncFromDungeon();
		}
	}

	public static void syncToDungeon() {
		if (activeSession != null) {
			activeSession.syncToDungeon();
		}
	}

	public static DungeonSession createIsolatedSession() {
		DungeonSession isolated = new DungeonSession();
		isolated.syncFromDungeon();
		return isolated;
	}

	public static void reset() {
		activeSession = new DungeonSession();
	}
}