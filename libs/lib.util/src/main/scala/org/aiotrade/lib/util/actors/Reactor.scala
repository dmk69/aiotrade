/*                     __                                               *\
 **     ________ ___   / /  ___     Scala API                            **
 **    / __/ __// _ | / /  / _ |    (c) 2007-2010, LAMP/EPFL             **
 **  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
 ** /____/\___/_/ |_/____/_/ | |                                         **
 **                          |/                                          **
 \*                                                                      */



package org.aiotrade.lib.util.actors

import org.aiotrade.lib.util.reactors.Event
import org.aiotrade.lib.util.reactors.Reactions
import org.aiotrade.lib.util.reactors.Stop


/**
 * The counterpart to publishers. Listens to events from registered publishers.
 */
trait Reactor extends scala.actors.Reactor[Event] {
  
  /**
   * All reactions of this reactor.
   */
  val reactions: Reactions = new Reactions.Impl += {
    case Stop => exit
    case _ => // it seems messages that have no corresponding reactions will remain in mailbox?, anyway, just add this wild reaction
  }
  
  start

  def stop {this ! Stop}

  /**
   * Listen to the given publisher as long as <code>deafTo</code> isn't called for 
   * them.
   */
  def listenTo(ps: Publisher*) = for (p <- ps) p.subscribe(this)
  /**
   * Installed reaction won't receive events from the given publisher anylonger.
   */
  def deafTo(ps: Publisher*) = for (p <- ps) p.unsubscribe(this)

  def act = loop {
    react {
      reactions
    }
  }
}