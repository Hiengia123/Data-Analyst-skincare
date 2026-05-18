# pyrefly: ignore [missing-import]

# pyrefly: ignore [missing-import]
from playwright.sync_api import sync_playwright
import json


all_reviews = []


def handle_response(response):

    url = response.url

    # Chỉ lấy XHR/API requests
    if response.request.resource_type != "xhr":
        return

    # Filter review APIs
    if "review" in url or "rating" in url:

        try:
            data = response.json()

            print("\n=== REVIEW API FOUND ===")
            print("URL:", url)

            # DEBUG structure trước
            # print(json.dumps(data, indent=2, ensure_ascii=False))

            # -------------------------
            # TÙY STRUCTURE LAZADA
            # -------------------------

            reviews = []

            # Thử nhiều structure phổ biến
            if "data" in data:
                reviews = data["data"].get("reviews", [])

            elif "model" in data:
                reviews = data["model"].get("items", [])

            elif "reviews" in data:
                reviews = data.get("reviews", [])

            # Parse reviews
            for review in reviews:

                parsed_review = {
                    "user": review.get("buyerName"),
                    "rating": review.get("rating"),
                    "comment": review.get("reviewContent"),
                }

                all_reviews.append(parsed_review)

                print(parsed_review)

        except Exception as e:
            print("ERROR:", e)


with sync_playwright() as p:

    browser = p.chromium.launch(
        headless=False
    )

    page = browser.new_page()

    # Listen responses
    page.on("response", handle_response)

    # Product detail page
    page.goto(
        "https://www.lazada.vn/products/pdp-i2290037532-s11100340893.html?c=&channelLpJumpArgs=&clickTrackInfo=query%253Amouse%253Bnid%253A2290037532%253Bsrc%253ALazadaMainSrp%253Brn%253Af63f6f4096636993c3dec59b2885f0df%253Bregion%253Avn%253Bsku%253A2290037532_VNAMZ%253Bprice%253A379000%253Bclient%253Adesktop%253Bsupplier_id%253A1000063447%253Bsession_id%253A%253Bbiz_source%253Ah5_hp%253Bslot%253A0%253Butlog_bucket_id%253A470687%253Basc_category_id%253A4460%253Bitem_id%253A2290037532%253Bsku_id%253A11100340893%253Bshop_id%253A321921%253BtemplateInfo%253A155386_D_E_G%2523-1_A3_C%2523&freeshipping=1&fs_ab=2&fuse_fs=&lang=en&location=Vietnam&price=3.79E%205&priceCompare=skuId%3A11100340893%3Bsource%3Alazada-search-voucher%3Bsn%3Af63f6f4096636993c3dec59b2885f0df%3BoriginPrice%3A379000%3BdisplayPrice%3A379000%3BsinglePromotionId%3A900000839212899%3BsingleToolCode%3AmillionSubsidy%3BvoucherPricePlugin%3A0%3Btimestamp%3A1778726041930&ratingscore=4.9411764705882355&request_id=f63f6f4096636993c3dec59b2885f0df&review=561&sale=2068&search=1&source=search&spm=a2o4n.searchlist.list.0&stock=1"
    )

    # Chờ page load
    page.wait_for_timeout(5000)

    print("\n=== START SMOOTH SCROLL ===")

    # Scroll từ từ
    for i in range(30):

        print(f"Scrolling step {i+1}")

        # Scroll nhỏ hơn
        page.mouse.wheel(0, 500)

        # Chờ frontend load
        page.wait_for_timeout(800)

    print("\n=== SCRAPING FINISHED ===")

    # Save reviews
    with open(
        "reviews.json",
        "w",
        encoding="utf-8"
    ) as f:

        json.dump(
            all_reviews,
            f,
            ensure_ascii=False,
            indent=2
        )

    print(f"\nSaved {len(all_reviews)} reviews")

    input("Press Enter to close...")

    browser.close()