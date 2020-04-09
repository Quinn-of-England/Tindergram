package com.example.soen341

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite::class)
@Suite.SuiteClasses(RegisterLoginTest::class, FunctionalityTest::class, SharedPrefManagerTest::class, CleanUpTest::class)
class TestSuite