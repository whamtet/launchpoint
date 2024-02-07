(ns simpleui.launchpoint.web.views.components.dropdown
    (:require
      [simpleui.launchpoint.web.views.components :as components]))

(defn- button [label]
  [:div
   [:button {:type "button"
             :class "bg-clj-blue p-1.5 rounded-lg text-white w-24 caret"
             :_ "on click halt the event then toggle .hidden on the next <div />"}
    label]])

(defn dropdown [label other-items]
  [:div.p-1
   (button label)
   [:div {:class "drop hidden rounded-lg border p-1 m-1"}
    (for [item other-items]
      [:a {:href ""}
       [:div {:class "hover:bg-slate-100"}
        item]])]])

(defn- button-up [label]
  [:div
   [:button {:type "button"
             :class "p-1.5 w-32 caret-up"
             :_ "on click halt the event then toggle .hidden on the previous .drop"}
    [:span.mr-2 label]]])

(defn dropup [label other-items]
  [:div
   [:div {:class "drop hidden rounded-lg border p-1.5 bg-white"}
    (for [item other-items]
      [:a {:href ""}
       [:div {:class "hover:bg-slate-100"}
        item]])]
   (button-up label)])
