plugins {
	id("io.komune.fixers.gradle.kotlin.jvm")
	kotlin("plugin.spring")
	kotlin("plugin.serialization")
	kotlin("kapt")
}

dependencies {
	api(project(Modules.data.s2.catalogueDraft.domain))

	implementation(project(Modules.commons))

	api(project(Modules.infra.meilisearch))
	implementation(project(Modules.infra.postgresql))
	implementation(project(Modules.infra.redis))

	Dependencies.Jvm.redisOm(::implementation, ::kapt)
	Dependencies.Jvm.s2Sourcing(::implementation)
}
