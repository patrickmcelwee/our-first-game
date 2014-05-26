package our_first_game.core;

import clojure.lang.RT;
import clojure.lang.Symbol;

import com.badlogic.gdx.*;
import com.badlogic.gdx.backends.iosrobovm.*;

import org.robovm.apple.foundation.*;
import org.robovm.apple.uikit.*;

public class IOSLauncher extends IOSApplication.Delegate {
	protected IOSApplication createApplication() {
		IOSApplicationConfiguration config = new IOSApplicationConfiguration();
		RT.var("clojure.core", "require").invoke(Symbol.intern("our-first-game.core"));
		try {
			Game game = (Game) RT.var("our-first-game.core", "our-first-game").deref();
			return new IOSApplication(game, config);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] argv) {
		NSAutoreleasePool pool = new NSAutoreleasePool();
		UIApplication.main(argv, null, IOSLauncher.class);
		pool.close();
	}
}
