package com.rmsftc.utils.telemetry.html;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HtmlTest {

    @Test
    void escape() {
        assertEquals("a &amp; b &lt;c&gt;", Html.escape("a & b <c>"));
    }

    @Test
    void tags() {
        assertEquals("<b>hi</b>", Html.bold("hi"));
        assertEquals("<i>hi</i>", Html.italic("hi"));
        assertEquals("hi<br/>", Html.line("hi"));
        assertEquals("<hr/>", Html.hr());
        assertEquals("<ul><li>a</li><li>b</li></ul>", Html.list("a", "b"));
    }

    @Test
    void htmlLoggerAccumulates() {
        HtmlLogger logger = new HtmlLogger();
        logger.log("a < b");
        logger.logHTML(Html.bold("done"));
        assertEquals("a &lt; b<br/><b>done</b>", logger.toString());
        logger.clear();
        assertEquals("", logger.toString());
    }
}
