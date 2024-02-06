(ns simpleui.launchpoint.web.views.components.dropdown
    (:require
      [simpleui.launchpoint.web.views.components :as components]))

(defn- button [label]
  [:div
   [:button {:type "button"
             :class "bg-clj-blue p-1.5 rounded-lg text-white w-24 caret"
             :_ "on click toggle .hidden on the next <div />"}
    label]])

(defn dropdown [label other-items]
  [:div.p-1
   (button label)
   [:div {:class "hidden rounded-lg border p-1.5 m-1 hover:bg-slate-100"}
    (for [item other-items]
      [:a {:href ""} item])]])
