/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// see LICENSE.txt in distribution for copyright and license information

package org.xmlpull.v1.tests;

//import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Simple test for minimal XML tokenizing
 *
 * @author Aleksander Slominski [http://www.extreme.indiana.edu/~aslom/]
 */
public class TestSimpleToken extends UtilTestCase {
    private XmlPullParserFactory factory;

    public TestSimpleToken(String name) {
        super(name);
    }

    protected void setUp() throws XmlPullParserException {
        factory = XmlPullParserFactory.newInstance(
            Thread.currentThread().getContextClassLoader().getClass(),
            System.getProperty(XmlPullParserFactory.DEFAULT_PROPERTY_NAME)
        );
        factory.setNamespaceAware(true);
        assertEquals(true, factory.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));
    }

    protected void tearDown() {
    }

    public void testSimpleToken() throws Exception {
        XmlPullParser xpp = factory.newPullParser();
        assertEquals(true, xpp.getFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES));

        // check setInput semantics
        assertEquals(XmlPullParser.START_DOCUMENT, xpp.getEventType());
        try {
            xpp.nextToken();
            fail("exception was expected of nextToken() if no input was set on parser");
        } catch(XmlPullParserException ex) {}

        xpp.setInput(null);
        assertEquals(XmlPullParser.START_DOCUMENT, xpp.getEventType());
        try {
            xpp.nextToken();
            fail("exception was expected of next() if no input was set on parser");
        } catch(XmlPullParserException ex) {}


        // check the simplest possible XML document - just one root element
        for(int i = 1; i <= 2; ++i) {
            xpp.setInput(new StringReader(i == 1 ? "<foo/>" : "<foo></foo>"));
            boolean empty = (i == 1);
            checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
            xpp.nextToken();
            checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, empty, 0);
            xpp.nextToken();
            checkParserStateNs(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
            xpp.nextToken();
            checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);
        }

        // one step further - it has content ...


        xpp.setInput(new StringReader(
                         "\n \r\n \n\r<!DOCTYPE titlepage SYSTEM \"http://www.foo.bar/dtds/typo.dtd\""+
                             "[<!ENTITY % active.links \"INCLUDE\">]>"+
                             "<!--c-->  \r\n<foo attrName='attrVal'>bar<!--comment-->"+
                             "&test;"+
                             "<?pi ds?><![CDATA[ vo<o ]]></foo> \r\n"));
        checkParserStateNs(xpp, 0, xpp.START_DOCUMENT, null, 0, null, null, null, false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.IGNORABLE_WHITESPACE, null, 0, null, null,
                           "\n \r\n \n\r", false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.DOCDECL, null, 0, null, null,
                           " titlepage SYSTEM \"http://www.foo.bar/dtds/typo.dtd\""+
                               "[<!ENTITY % active.links \"INCLUDE\">]", false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.COMMENT, null, 0, null, null, "c", false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.IGNORABLE_WHITESPACE, null, 0, null, null, "  \r\n", false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.START_TAG, null, 0, "", "foo", null, false, 1);
        checkAttribNs(xpp, 0, null, "", "attrName", "attrVal");
        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.TEXT, null, 0, null, null, "bar", false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.COMMENT, null, 0, null, null, "comment", false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.ENTITY_REF, null, 0, null, null, "test", false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.PROCESSING_INSTRUCTION, null, 0, null, null, "pi ds", false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 1, xpp.CDSECT, null, 0, null, null, " vo<o ", false, -1);

        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.END_TAG, null, 0, "", "foo", null, false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.IGNORABLE_WHITESPACE, null, 0, null, null,
                           " \r\n", false, -1);
        xpp.nextToken();
        checkParserStateNs(xpp, 0, xpp.END_DOCUMENT, null, 0, null, null, null, false, -1);


    }

    public static void main (String[] args) {
        junit.textui.TestRunner.run (new TestSuite(TestSimple.class));
    }

}
