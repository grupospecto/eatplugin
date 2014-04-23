EATPlugin - Enterprise Architect Traceability plugin for Eclipse

The goal of this project is to provide an easy way of filling Enterprise Architect's traceability matrix.
EAT plugin allow developers to access use cases planned in EA directly within Eclipse, linking them with currently editing java class.
Once a class is linked with one or more use cases, the class is commented (JavaDoc) with Use Case(s) ID(s) and Enterprise Architect traceability matrix is updated with newly created relationship.

In order to use this plugin, you must change JDBC driver class/host/authoriry according to your reality.
These changes can be made in file /src/resources/jdbc.properties.

Values for properties in the file /src/resources/i18n.properties may be changed to fit your locale, as you desire.

TODO: This plugin still doesn't reconize project's relative path, so the default value taken is 'src', which can be changed in the class 'br.com.specto.plugin.tracer.util.Util': final String TAG = "src";
Contributions are welcome, as well feedbacks ;-)

We hope you enjoy using this plugin and may it be useful to you!

Eduardo Kreuch / Jean Carlo Rossa Hauck
