
package org.owasp.webgoat.plugin;

import org.apache.ecs.Element;
import org.apache.ecs.ElementContainer;
import org.apache.ecs.StringElement;
import org.apache.ecs.html.A;
import org.apache.ecs.html.BR;
import org.apache.ecs.html.Div;
import org.apache.ecs.html.Form;
import org.apache.ecs.html.H1;
import org.apache.ecs.html.IMG;
import org.apache.ecs.html.Input;
import org.apache.ecs.html.TD;
import org.apache.ecs.html.TR;
import org.apache.ecs.html.Table;
import org.owasp.webgoat.lessons.Category;
import org.owasp.webgoat.lessons.LessonAdapter;
import org.owasp.webgoat.session.WebSession;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;


/***************************************************************************************************
 * 
 * 
 * This file is part of WebGoat, an Open Web Application Security Project utility. For details,
 * please see http://www.owasp.org/
 * 
 * Copyright (c) 2002 - 20014 Bruce Mayhew
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 * 
 * Getting Source ==============
 * 
 * Source for this application is maintained at https://github.com/WebGoat/WebGoat, a repository for free software
 * projects.
 * 
 * For details, please see http://webgoat.github.io
 * 
 * @author Sherif Koussa <a href="http://www.softwaresecured.com">Software Secured</a>
 * @created October 28, 2006
 */

public class DOMInjection extends LessonAdapter
{

    private final static Integer DEFAULT_RANKING = new Integer(10);
    private final static String ACTIVATE = "Activate!";
    private final static String KEY = "key";

    public final static A MAC_LOGO = new A().setHref("http://www.softwaresecured.com").addElement(new IMG("images/logos/softwaresecured.gif").setAlt("Software Secured").setBorder(0).setHspace(0).setVspace(0));
    
    private final static String key = "K1JFWP8BSO8HI52LNPQS8F5L01N";

    public void handleRequest(WebSession s)
    {
        try
        {
            String userKey = s.getParser().getRawParameter(KEY, "");
            String fromAJAX = s.getParser().getRawParameter("from", "");
            if (fromAJAX.equalsIgnoreCase("ajax") && userKey.length() != 0 && userKey.equals(key))
            {
                s.getResponse().setContentType("text/html");
                s.getResponse().setHeader("Cache-Control", "no-cache");
                PrintWriter out = new PrintWriter(s.getResponse().getOutputStream());

                out.print("document.form.SUBMIT.disabled = false;");
                out.flush();
                out.close();
                return;
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        Form form = new Form(getFormAction(), Form.POST).setName("form").setEncType("");

        form.addElement(createContent(s));

        setContent(form);
    }

    protected Element createContent(WebSession s)
    {

        ElementContainer ec = new ElementContainer();

        if (checkSuccess(s))
        {
            makeSuccess(s);
        }

        ec.addElement("<script src='" + buildJsPath(s, "dom_injection.js") + "'> </script>");
        ec.addElement(new BR().addElement(new H1().addElement("Welcome to WebGoat Registration Page:")));
        ec.addElement(new BR()
                .addElement("Please enter the license key that was emailed to you to start using the application."));
        ec.addElement(new BR());
        ec.addElement(new BR());
        Form form = new Form(getLink(), Form.POST);
        Table t1 = new Table().setCellSpacing(0).setCellPadding(0).setBorder(0).setWidth("70%").setAlign("center");

        TR tr = new TR();
        tr.addElement(new TD(new StringElement("License Key: ")));

        Input input1 = new Input(Input.TEXT, KEY, "");
        input1.setID(KEY);
        input1.addAttribute("onkeyup", "validate('" + getLink() + "');");
        tr.addElement(new TD(input1));
        t1.addElement(tr);

        tr = new TR();
        tr.addElement(new TD("&nbsp;").setColSpan(2));

        t1.addElement(tr);

        tr = new TR();
        Input b = new Input();
        b.setType(Input.SUBMIT);
        b.setValue(ACTIVATE);
        b.setName("SUBMIT");
        b.setID("SUBMIT");
        b.setDisabled(true);
        tr.addElement(new TD("&nbsp;"));
        tr.addElement(new TD(b));

        t1.addElement(tr);
        form.addElement(t1);
        ec.addElement(form);
        Div div = new Div();
        div.addAttribute("name", "MessageDiv");
        div.addAttribute("id", "MessageDiv");
        ec.addElement(div);

        return ec;
    }

    private boolean checkSuccess(WebSession s) {
        String submit = s.getRequest().getParameter("SUBMIT");
        return submit != null && submit.equals(ACTIVATE);
    }

    public Element getCredits()
    {
        return super.getCustomCredits("Created by Sherif Koussa&nbsp;", MAC_LOGO);
    }

    protected Category getDefaultCategory()
    {

        return Category.AJAX_SECURITY;
    }

    protected Integer getDefaultRanking()
    {

        return DEFAULT_RANKING;
    }

    protected List<String> getHints(WebSession s)
    {

        List<String> hints = new ArrayList<String>();
        hints.add("This page is using XMLHTTP to comunicate with the server.");
        hints.add("Try to find a way to inject the DOM to enable the Activate button.");
        hints.add("Intercept the reply and replace the body with document.form.SUBMIT.disabled = false;");
        return hints;
    }

    public String getTitle()
    {
        return "DOM Injection";
    }

}
