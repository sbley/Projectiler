package de.saxsys.android.projectiler;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Start {
	private static final int SCHEMA_VERSION = 3;

	private static void addTables(final Schema schema) {
		schema.enableKeepSectionsByDefault();

		// TreeSpecies
		final Entity entityTrack = schema.addEntity("Track");
		entityTrack.addIdProperty();
		entityTrack.addStringProperty("projectName").notNull();
		entityTrack.addDateProperty("timestamp").notNull();
		entityTrack.addDateProperty("startdDate").notNull();
		entityTrack.addDateProperty("endDate").notNull();


	}

	public static void main(final String[] args) throws Exception {
		final Schema schema = new Schema(SCHEMA_VERSION,
				"de.saxsys.android.projectiler.app.generatedmodel");

		addTables(schema);

		new DaoGenerator().generateAll(schema,
				"../Projectiler\\app\\src\\main\\java");
	}
}
