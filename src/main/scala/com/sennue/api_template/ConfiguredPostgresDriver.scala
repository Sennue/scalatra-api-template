// Reference: https://github.com/tminglei/slick-pg

package com.sennue.api_template

import slick.driver.PostgresDriver
import com.github.tminglei.slickpg._

trait ConfiguredPostgresDriver extends PostgresDriver
                          with PgArraySupport
                          with PgDateSupport
                          with PgRangeSupport
                          with PgHStoreSupport
                          //with PgPlayJsonSupport
                          with PgSearchSupport
                          with PgPostGISSupport {
  //override val pgjson = "jsonb" //to keep back compatibility, pgjson's value was "json" by default

  override lazy val Implicit = new ImplicitsPlus {}
  override val simple = new SimpleQLPlus {}

  //////
  trait ImplicitsPlus extends Implicits
                        with ArrayImplicits
                        with DateTimeImplicits
                        with RangeImplicits
                        with HStoreImplicits
                        //with JsonImplicits
                        with SearchImplicits
                        with PostGISImplicits

  trait SimpleQLPlus extends SimpleQL
                        with ImplicitsPlus
                        with SearchAssistants
                        with PostGISAssistants
}

object ConfiguredPostgresDriver extends ConfiguredPostgresDriver

