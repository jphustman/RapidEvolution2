package rapid_evolution.threads;

import rapid_evolution.RapidEvolution;
import rapid_evolution.ui.RapidEvolutionUI;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2004</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class SearchThread extends Thread {
  public SearchThread() {}
  public void run() {
    RapidEvolutionUI.instance.SearchRoutine();
  }
}
