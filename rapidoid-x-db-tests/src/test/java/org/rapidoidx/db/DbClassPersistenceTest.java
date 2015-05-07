package org.rapidoidx.db;

/*
 * #%L
 * rapidoid-x-db-tests
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.concurrent.atomic.AtomicInteger;

import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.config.Conf;
import org.rapidoid.util.OptimisticConcurrencyControlException;
import org.rapidoid.util.Rnd;
import org.rapidoid.util.UTILS;
import org.rapidoidx.db.DB;
import org.rapidoidx.db.model.Person;
import org.testng.annotations.Test;

@Authors("Nikolche Mihajlovski")
@Since("3.0.0")
public class DbClassPersistenceTest extends DbTestCommons {

	@Test
	public void testPersistence() {

		final int count = 10000;

		System.out.println("inserting...");

		UTILS.startMeasure();

		UTILS.benchmarkMT(Conf.cpus(), "insert", count, new Runnable() {
			@Override
			public void run() {
				DB.insert(new Person("abc", -1));
			}
		});

		System.out.println("updating...");

		final AtomicInteger occN = new AtomicInteger();

		UTILS.benchmarkMT(10, "update", count, new Runnable() {
			@Override
			public void run() {
				int id = Rnd.rnd(count) + 1;
				Person person = new Person("x", id * 100);
				person.version(DB.getVersionOf(id));

				try {
					DB.update(id, person);
				} catch (OptimisticConcurrencyControlException e) {
					eq(e.getRecordId(), id);
					occN.incrementAndGet();
				}
			}
		});

		System.out.println("Total OCC exceptions: " + occN.get());
		isTrue(occN.get() < count / 10);

		DB.shutdown();
		DB.start();

		checkDb(count);
		checkDb(count);
		checkDb(count);
	}

	private void checkDb(final int count) {
		UTILS.endMeasure("total");

		eq(DB.size(), count);

		for (int id = 1; id <= count; id++) {
			Person p = DB.get(id);
			isTrue(p.id() == id);
			isTrue((p.name.equals("abc") && p.age == -1) || (p.name.equals("x") && p.age == id * 100));
		}

		DB.shutdown();
	}

}