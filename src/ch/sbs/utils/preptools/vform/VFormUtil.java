package ch.sbs.utils.preptools.vform;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VFormUtil {

	private static final String[] forms = new String[] { "Sie", "Ihrethalber",
			"Ihretwegen", "Ihren", "Ihrem", "Ihres", "Ihre", "Ihr", "Ihnen", };

	private static final String[] formsOldSpelling = new String[] { "Du",
			"Dich", "Dir", "Deinethalber", "Deinetwegen", "Deinen", "Deinem",
			"Deines", "Deine", "Dein", "Eurethalber", "Euretwegen", "Euren",
			"Eurem", "Eures", "Eure", "Euer", "Euch" };

	private static final String[] allForms;

	static {
		allForms = new String[forms.length + formsOldSpelling.length];
		int i = 0;
		for (final String form : forms) {
			allForms[i++] = form;
		}
		for (final String form : formsOldSpelling) {
			allForms[i++] = form;
		}
	}

	/*
	 * 1. VForms
	 * =========
	 * 1.1. Recherche:
	 * -------------
	 * 16.12.2010 - Mischa Kuenzle
	 * Die Formen in formsOldSpelling werden nach neuer Rechtschreibung nicht 
	 * mehr gross geschrieben. Deshalb:
	 * 
	 * 1.1.1. Diskriminante Old/New
	 * --------------------------
	 * Suche alle Forms in formsOldSpelling, ob sie irgendwo im Text ausser 
	 * am Satzanfang vorkommen. Wenn ja, ist das Dokument nach alter 
	 * Rechtschreibung verfasst, und das Dokument sollte nach vforms im Array 
	 * oldSpelling abgesucht werden. Wenn nicht, frage diese Formen gar nicht 
	 * ab, sondern nur diejenigen im Array "forms".
	 * 
	 * 1.1.2. Binnen-Höflichkeitsformen
	 * ------------------------------
	 * 22.12.10 - Christian Waldvogel 
	 * Die "Binnen-Höflichkeitsformen" können in jedem Fall schon
	 * als vforms markiert werden, der Benutzer muss diese nicht auch noch
	 * abnicken!
	 * Es geht dann nur noch um die potentiellen vForms am Satzanfang.
	 * 
	 * 1.1.3. Frage: Was heisst Satzanfang?
	 * 
	 * 
	 * 
	 * 23.12.2010 - Mischa Kuenzle
	 *	
	 * 1.1.3.1. EBNF für Satzende
	 * ------------------------
	
	 Variations:
	 1. Punkt                        Whitespace
	 2. Punkt                        Whitespace QuoteSign
	 3. Punkt QuoteSign              Whitespace
	 4. Punkt QuoteSign              Whitespace QuoteSign

	 5. Punkt           ClosingBrace Whitespace
	 6. Punkt           ClosingBrace Whitespace QuoteSign 
	 7. Punkt QuoteSign ClosingBrace Whitespace
	 8. Punkt QuoteSign ClosingBrace Whitespace QuoteSign 
	
	 9. Punkt           ClosingBrace            QuoteSign Whitespace

	10. (1., 2., 3., 4.) -> Punkt [QuoteSign]                Whitespace [QuoteSign]
	11. (5., 6., 7., 8.) -> Punkt [QuoteSign]  ClosingBrace  Whitespace [QuoteSign]
	12. (10., 11.)       -> Punkt [QuoteSign] [ClosingBrace] Whitespace [QuoteSign]
	
	-> 9 | 12
	
	->  Punkt ((            ClosingBrace              QuoteSign Whitespace)|
		      ([QuoteSign] [ClosingBrace] Whitespace [QuoteSign]))

	PhraseEnd = Punkt (( ClosingBrace QuoteSign Whitespace)|
		      ([QuoteSign] [ClosingBrace] Whitespace [QuoteSign])
		      
	Problem with this syntax: We need 2-symbol lookahead!
	If Punkt [QuoteSign] is followed by ClosingBrace, we need to look at one more sign:
	Is it QuoteSign -> we're in the branch 9.
	Is it Whitespace -> we're in the branch 12.
	Is it something else -> we're not in a PhraseEnd.
	
	Punkt = "." | ":" | "!" | "?".
	
	QuoteSign = '"' | "&quot;" | "'" | "&apos;" | "«" | "»" | "‹" | "›" | "〈" | "〉".
	
	ClosingBrace = ")" | "]" | "}".
	
	 * 1.1.3.2. DOM vs. Text
	 * -------------------

	Achtung: Warum wir auf DOM-Ebene und nicht Text-Ebene arbeiten sollten:
	Dies ist ein Beispiel für Satzende-Variation #2.
	Würde aber von der Regexp nicht erkannt, weil uns die pagenum in die Quere
	kommt.
	Beispiel 1 (dtbook-strukturelemente, welche unsere EBNF-Satzende-Grammatik stören):
	<p>
	Er ging zur Arbeit. <pagenum id="page-7" page="normal">7</pagenum> 
	»Sie sind zu spät, Herr Müller!« rief sein Chef.
	</p>
	Beispiel 2 (Markup, welche die EBNF-Grammatik stört):
	<p>
	Er ging zur <em>Migros.</em> 
	»Sie schon wieder, Herr Müller!« rief die Kassiererin.
	</p>
	
	-> Nachteile von DOM: Wie finden wir von der DOM-Repräsentation zurück in den Text?
	-> Ist es erlaubt, dass wir den Text parsen, im DOM verändern und dann zurückschreiben?
	-> dabei könnte Formatierung anders rauskommen.
	-> dabei könnten die Kommentare, Processing Instructions (sonstiges?) verloren gehen.
	-> DOM-Parser im Projekt DtBookParser (warum DOM? Sax nicht, weil ich sonst state
	   zwischen Events pflegen muss)
	
	 * 1.1.3.3. Auf Dtbook-Strukturebene
	 * -------------------------------
	 * - Am Anfang eines
	 * - Paragraphs (<p>)
	 * - Headings (<h[1-6]>)
	 * - <brl:running-line>
	 * - <brl:toc-line>
	 * - Doctitles (<doctitle>)
	 * - Poemzeile (<line>)
	 * - <li> (das keine <lic> enthält)
	 * - <lic>
	 * - <byline>
	 * 
	 * 
	 * Frage: Wann soll dieser Test (1.1.) ablaufen?
	 * Mögliche Zeitpunkte:
	 * 1. Beim Oeffnen eines Dokuments vom Typ dtbook.
	 * 1.1 das Plugin merkt sich *pro Dokument*: boolean oldSPelling.
	 *   1.2 sobald Benutzer nach vforms sucht, wird aufgrund das Flags
	 *       oldSpelling entschieden, ob nur forms oder auch formsOldSpelling
	 *       relevant sind.
	 *   Nachteil: 
	 *   1. Jedes dtbook Dokument wird beim Oeffnen abgesucht, obwohl
	 *      Benutzer evtl. gar nicht vforms checken möchte.
	 *   2. Wenn Benutzer Aenderungen am Dokument vornimmt, welche das Prädikat
	 *      oldSpelling beeinflussen. -> nach jeder Aenderung müsste das
	 *      Prädikat wieder geprüft werden.
	 *      
	 * 2. Sobald Benutzer vforms zu prüfen beginnt.
	 * 2.1 dies setzt voraus, dass wir wissen, wann er *beginnt* und wann er
	 *     fertig ist: Thematik "modal".
	 * 2.2 z.B. Menü PrepTools>VForms: Sobald Benutzer das wählt, wird:
	 *   2.2.1 die VForm Toolbox aktiviert (_3)
	 *   2.2.2 Prüfung auf oldSpelling durchgeführt und für das aktuelle 
	 *         Dokument vermerkt.
	 *   2.2.3 Die erste VForm gesucht, Benutzer kann wählen Find/Accept
	 *         
	 *   Frage: Wenn Benutzer Dokument wechselt, müsste Toolbar angepasst werden.
	 *   Problem: es gibt showToolbar (_3) aber nicht hideToolbar (_2)
	 *   
	 *  TODO: keys für Toolbar! (_4)
	 *  TODO: Bereits markierte Form überspringen.
	 *  TODO: Find VForms: muss sich merken, ob der Prozess neu gestartet wurde
	 *        wenn Ja: Cursor an Dokumentanfang setzen (_5) und von dort suchen.
	 *        
	 *  Optional:
	 *	- Während des Prozesses keine Textänderungen zulassen
	 *	- Keine Cursorbewegungen
	 *	- evtl. andere Toolbars wegnehmen (_2) bis der Prozess abgeschlossen ist.
	 *    
	 *  Wenn in PrepTools VForms gewählt wird, und vor Beendigung
	 *  ein anderes Tool gewählt wird, sollte eine Abfrage kommen: Soll die
	 *  VForm-Suche vorzeitig abgebrochen werden?
	 *  Ebenso:
	 (- anderes Dokument wählen: doch nicht: Cursor-Position bleibt erhalten)
	 (- anderes Dokument öffnen: doch nicht: Cursor-Position bleibt erhalten)
	 *
	 * 1.2. Hypothese:
	 * ------------
	 * 1. Es gibt 3 (4) Buttons im Toolbar:
	 * - StartVForm
	 * - FindVForm
	 * - AcceptVForm
	 * (- ContinueVForm)
	 * 
	 * Pro Dokument wird gespeichert, ob vForm-Prozess durchgeführt wird.
	 * Solange das nicht der Fall ist, ist nur StartVForm enabled (_e), FindVorm,
	 * AcceptVForm disabled. Wenn das Dokument kein dtbook ist, sind alle
	 * drei Buttons disabled.
	 * Die ToolTips geben Hinweis, warum sie disabled sind (nice). (_1)
	 * 
	 * 2. Es liegt dtbook Dokument vor:
	 * DONE (nice: LED becomes green _g)
	 * DONE Benutzer klickt StartVForm. Flag isProcessingVforms = true; für dieses Dokument.
	 * DONE (nice: LED becomes yellow _g)
	 * 
	 * Gesamtes Dokument wird nach formsOldSpelling *ausser am Satzanfang* 
	 * abgesucht. Wenn etwas gefunden wird: Dialogbox mit Statistik anzeigen, wie oft
	 * formsOldSpelling auftreten, sowie der Frage, ob Benutzer alle alten Formen
	 * abnicken möchte. Wenn Benutzer möchte, wird isOldSpelling = true gesetzt und
	 * die Regex beinhaltet alte und neue vforms (Vereinigung von forms und formsOldSpelling).
	 * Alle Binnenvforms (sowohl die in formsOldSpelling und die in forms).
	 * (nice: Es werden dem Benutzer für jede der forms in formsOldSpelling die Häufigkeiten
	 * angezeigt, und er kann mittels Checkbox wählen, welche er abnicken möchte und welche
	 * nicht)
	 * werden schon als solche markiert <brl:v-form>. Ausserdem wird die erste 
	 * potentielle vForm gesucht, gehighlightet (_d) und die Buttons FindVForm,
	 * AcceptVForm werden enabled. Die Erwartung ist nun, dass der Benutzer nur anhand
	 * der Buttons Find/Accept durch das Dokument bis ans Ende navigiert. Wenn er ans
	 * 
	 * Ende kommt (_6): Dialogbox: Dokument-Ende erreicht mit Angabe, 
	 * welches Tool/Prozess angeben, z.B.: VForms, Parentheses, sowie Statistik.
	 * Für dieses Dokument wird ausserdem Flag isProcessingVforms = false
	 * Benutzer in der Dialogbox eine Checkbox anbieten, ob Statistik in einem
	 * Kommentar <!-- --> oben eingefügt werden soll.
	 * DONE Unterstützung des Prozesses mit einer "LED" im Toolbar (nice):
	 *      Document not yet vformed: green, Document in progress: yellow, Document
	 *      done: blue.
	 * (nice: während der Bearbeitung sieht der Benutzer einen Progressbar und eine
	 * estimated completion time, welche aufgrund der Statistik und seiner persönlichen
	 * Arbeitsgeschwnindigkeit berechnet wird. Nach einem Timeout wird die Zeit nicht
	 * mehr berücksichtigt.)
	 * 
	 * Ausnahmen: Wenn Benutzer während des Prozesses (isProcessingVforms == true)
	 * DONE- StartVForm klickt, wird Dialogbox angezeigt "Start over?" 
	 * DONE (verworfen: startVForm Button disablen)
	 * - Wenn er Dokument schliesst (editorClosed _9), Dialogbox anzeigen "vorzeitig abbrechen?"
	 * - Wenn er Applikation beendet (applicationClosing _c), Dialogbox anzeigen "vorzeitig abbrechen?"
	 * - Cursor selber plaziert: (_7) Toolbar hat einen weiteren Button "Continue".
	 *   Implemenentation: für jedes Dokument wird die zuletzt bearbeitete Stelle 
	 *   vermerkt (_f).
	 *   Wenn der Cursor bewegt wurde, kann anhand dieser Stelle der vform-Prozess wieder
	 *   aufgenommen werden. 
	 * - Text ändert: siehe "Cursor selber plaziert" (_8)?
	 * - anderes Tool wählt: Dialogbox anzeigen "vorzeitig abbrechen?"
	 * - anderes Dokument öffnet (editorOpened _a): siehe "Cursor selber plaziert"
	 * - zu anderem Dokument umschaltet (editorSelected _b):siehe "Cursor selber plaziert"
	 * 
	 * Erhoffte Features vom Oxygen API
	 * _1: Dynamisches Aendern von Tooltips für Toolbar buttons?
	 * _2: Entfernen von Toolbars (anscheinend nicht möglich)
	 * _3: Anzeigen von Toolbars (StandalonePluginWorkspace.showToolbar)
	 * _4: Tastaturaequivalent von Toolbar Buttons?
	 * _5: Cursor an Dokumentanfang setzen: Ja
	 * _6: Bekommen wir es mit, wenn das Dokument-Ende erreicht wird? Ja
	 * _7: Bemerken von Cursorbewegung?
	 * _8: Bemerken von Textänderungen?
	 * _9: Bemerken von Editorclose? Ja, aber ich nicht eingreifen!
	 * _a: Bemerken von EditorOpen? Ja. -> aber nicht bei "Revert"!
	 * _b: Bemerken von EditorSelect? Ja, aber weiss ich welcher Editor vorher gewählt war?
	 * _c: Bemerken von applicationClosing? Ja, ich kann eingreifen: return false
	 * _d: Highlighten (Selektieren) von Text: Ja.
	 * _e: toolbarbuttons enablen/disablen: Ja.
	 * _f: Textmarke ("Bookmark"), welche unabhängig vom Einfügen oder Löschen
	 *     immer auf die vermerkte Stelle zeigt? (javax.swing.text.Document)
	 * _g: "LED" in the toolbar with configurable colour: Yes
	 * _h: Can I define what an atomic command is? (I'd like to define that
	 *     inserting </brl:v-form> after and <brl:v-form> before a word is
	 *     *one* command (not two separate ones), so an Undo will remove
	 *     both elements.
	 *     
	 * Empirical findings on WSEditorChangeListener:
	 * closing a document, if there's at least one other document open,
	 * we get:
	 * -> editorSelected + url next
	 * -> editorClosed + url closing
	 * -> editorSelected + url next

	 * closing a document, if this is the last document open,
	 * we get:
	 * -> editorClosed + url closing
	 * -> editorSelected + url null
	 * 
	 * Opening a document, we get:
	 * -> editorSelected + new url
	 * -> editorOpened + new url
	 * 
	 * Selecting a document (only possible if there's more than 1)
	 * -> editorSelected + new url
	 * 
	 * Starting the app we get:
	 * -> editorSelected + null (that's all we get when no docs where open)
	 * For each open file:
	 * -> editorSelected + url
	 * -> editorOpened + url
	 * At the end again for each open file:
	 * -> editorSelected + url
	 * 
	 * Conclusion:
	 * editorSelected always gets called when a document is opened.
	 * 
	 * 
	 * 2. Balanced Parentheses:
	 * ========================
	 * http://redmine.sbszh.ch/issues/show/1059
	 * 
	 */

	private static final Pattern vFormPattern;

	static {

		final StringBuffer sb = new StringBuffer("(?:"); // non-capturing group,
															// see
		// http://download.oracle.com/javase/1.5.0/docs/api/java/util/regex/Pattern.html#special
		for (final String form : allForms) {
			sb.append(form);
			sb.append("|");
		}
		sb.setLength(sb.length() - 1); // chop off last "|"
		sb.append(")\\b"); // make sure we don't match substrings.
		vFormPattern = Pattern.compile(sb.toString());
	}

	public static String replace(final String theText) {
		return vFormPattern.matcher(theText).replaceAll(
				"<brl:v-form>$0</brl:v-form>");
		// Group zero always stands for the entire expression.
		// http://download.oracle.com/javase/1.5.0/docs/api/index.html?java/util/regex/Matcher.html
	}

	public static Match find(final String text, int i) {
		final Matcher m = vFormPattern.matcher(text);
		return m.find(i) ? new Match(m.start(), m.end()) : NULL_MATCH;
	}

	public static boolean matches(final String text) {
		return text != null && vFormPattern.matcher(text).matches();
	}

	public static class Match {
		public Match(int start, int end) {
			startOffset = start;
			endOffset = end;
		}

		public int startOffset;
		public int endOffset;
	}

	public static final String wrap(final String theString) {
		return wrap(theString, "brl:v-form");
	}

	public static final String wrap(final String theString,
			final String theElement) {
		final StringBuilder sb = new StringBuilder("<");
		sb.append(theElement);
		sb.append(">");
		sb.append(theString);
		sb.append("</");
		sb.append(theElement);
		sb.append(">");
		return sb.toString();
	}

	public static final Match NULL_MATCH = new Match(-1, -1);
}
