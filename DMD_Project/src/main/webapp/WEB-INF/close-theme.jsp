<%@ page import="dmd.ShortData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%! String authors = ""; %>

<div class="panel-body">

    <% for (ShortData d : (ArrayList<ShortData>) request.getAttribute("closeTheme")) {
        authors = "";
        for (int i = 0; i < d.authors.size() - 1; ++i)
            authors += d.authors.get(i) + ", ";
        authors += d.authors.get(d.authors.size() - 1);

    %>

    <h5><a href="/article?id=<%=d.id%>"><%=d.title%>
    </a><br>
        <small><%=authors%>
        </small>
    </h5>
    <p><%=d.title%>
    </p>

    <p>
        <small><%=d.published%>
        </small>
    </p>

    <hr>

    <% } %>

</div>
