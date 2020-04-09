package com.example.soen341

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite::class)
@Suite.SuiteClasses(RegisterLogin::class, Functionality::class, SharedPrefManagerTest::class, CleanUp::class)
class TestSuite