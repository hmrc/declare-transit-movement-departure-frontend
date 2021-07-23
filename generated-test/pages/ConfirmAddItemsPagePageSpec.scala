package pages

import pages.behaviours.PageBehaviours

class ConfirmAddItemsPagePageSpec extends PageBehaviours {

  "ConfirmAddItemsPagePage" - {

    beRetrievable[Boolean](ConfirmAddItemsPagePage)

    beSettable[Boolean](ConfirmAddItemsPagePage)

    beRemovable[Boolean](ConfirmAddItemsPagePage)
  }
}
