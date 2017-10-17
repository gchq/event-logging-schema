package event.logging.transformation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerFactory;


public final class TransformerFactoryFactory {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(TransformerFactoryFactory.class);

	private static final String SAXON_TRANSFORMER_FACTORY = "net.sf.saxon.TransformerFactoryImpl";
	private static final String IMP_USED = "The transformer factory implementation being used is: ";
	private static final String END = "\".";
	private static final String SYSPROP_SET_TO = "System property \"javax.xml.transform.TransformerFactory\" set to \"";
	private static final String SYSPROP_NOT_SET = "System property \"javax.xml.transform.TransformerFactory\" not set.";
	private static final String SYSPROP_TRANSFORMER_FACTORY = "javax.xml.transform.TransformerFactory";

	static {
		final String factoryName = System
				.getProperty(SYSPROP_TRANSFORMER_FACTORY);
		if (factoryName == null) {
			LOGGER.info(SYSPROP_NOT_SET);

			System.setProperty(SYSPROP_TRANSFORMER_FACTORY,
					SAXON_TRANSFORMER_FACTORY);
		} else {
			final StringBuilder sb = new StringBuilder();
			sb.append(SYSPROP_SET_TO);
			sb.append(factoryName);
			sb.append(END);
			LOGGER.debug(sb.toString());
		}

		final TransformerFactory factory = TransformerFactory.newInstance();
		final StringBuilder sb = new StringBuilder();
		sb.append(IMP_USED);
		sb.append(factory.getClass().getName());
		LOGGER.debug(sb.toString());
	}

	private TransformerFactoryFactory() {
		// Utility class.
	}

	public static TransformerFactory newInstance() {
		return TransformerFactory.newInstance();
	}
}
