To manually generate the HTML documentation for a schema do the following:

> NOTE: This requires you have a licensed copy of Oxygen XML Developer locally and assumes you are running the commands from the root of this repo.

1. Create an output dir, e.g.
   `mkdir -p /tmp/schema_docs_3.5.2/`
1. Download the appropriate version of the schema (likely the client variant) from https://github.com/gchq/event-logging-schema/releases into this dir.
1. Generate the docs:
   `SCHEMA=/tmp/schema_doc_3.5.2/event-logging-v3-client.xsd; OXYGEN_DIR=/path/to/oxygen; "${OXYGEN_DIR}/schemaDocumentation.sh" "${SCHEMA}" -cfg:oxygen_schema_docs_settings.xml`
1. This will output the docs to:
   `/tmp/schema_docs_3.5.2/docs`
1. Replace the footer in the html pages:
   `find /tmp/schema_docs_3.5.2 -name "*.html" | xargs sed -i 's/XML Schema documentation generated by.*XML Editor\./event-logging v3.5.2/'`


To make it possible to navigate directly to a part of the schema but still retain the index pane, replace `index.html` with this:

```html
<!DOCTYPE html
  PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Schema documentation for component event-logging-v4-client.xsd</title>
    <link rel="stylesheet" href="docHtml.css" type="text/css" />
    <script>
      function loadPage() {
        const params = new URLSearchParams(location.search);
        var dest = params.get('dest');
        if (dest) {
          var iframe = document.getElementsByName('mainFrame')[0];
          if (iframe) {
            iframe.setAttribute("src", dest);
          } else {
            console.log("No iframe");
          }
        }
      }
    </script>
  </head>
  <frameset cols="20%, 80%" onload="loadPage();">
    <frame name="indexFrame" src="index.indexListcomp.html" />
    <frame name="mainFrame" src="event-logging-v4-client_xsd_Element_evt_Event.html" />
  </frameset>
</html>
```
