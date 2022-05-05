# Criteria

This is used to describe the criteria applied in any kind of search or filter operation along with the returned results, if known.  The `<Query>` element is used to record the actual query terms used, either in its raw form (e.g. SQL or XPath), as a nested hierarchy of operators and expressions or as a set of simple `<Include>` or `<exclude>` elements.

For example, this may be used to describe the criteria for a query that has been stored for re-use, e.g. an `<Update>/<Criteria>` event where a user is making changes to a stored query but not actually executing the query.

See [Complete Examples](../completeExamples/README.md) for example events.
