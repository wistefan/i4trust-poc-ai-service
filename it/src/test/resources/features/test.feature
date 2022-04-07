Feature: Happy cattle is updated via an authorized subscription in smart shepard

  Scenario: A happy cattle is created in happy-cattle and send to smart shepard.
    Given the setup is running.
    And endpoint auth is configured for happy cattle.
    When subscription to smart shepard is created.
    And a happy cattle is created in happy-cattle.
    Then the cattle is present in smart shepard.