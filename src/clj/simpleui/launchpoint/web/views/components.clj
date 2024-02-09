(ns simpleui.launchpoint.web.views.components)

(defn input
  ([type {:keys [label name value autocomplete asterisk]}]
   [:div
    [:div.p-1 label (when asterisk [:span.text-red-500 " *"])]
    [:div.p-1
     [:input.w-full.p-1
      {:type type
       :name name
       :value value
       :autocomplete autocomplete
       :required true
       :placeholder label}]]])
  ([type label name]
   (input type {:label label :name name}))
  ([type label name value]
   (input type {:label label :name name :value value}))
  ([type label name value asterisk]
   (input type {:label label :name name :value value :asterisk asterisk})))

(def text (partial input "text"))
(def email (partial input "email"))

(defn password
  ([label name]
   (input "password" label name))
  ([label name asterisk]
   (input "password" {:label label
                      :name name
                      :asterisk asterisk
                      :autocomplete "off"})))

(defn hidden [name value]
  [:input {:type "hidden" :name name :value value}])

(defn submit [label]
  [:div.p-1
   [:input {:type "submit"
            :class "bg-clj-blue p-1.5 rounded-lg text-white w-24"
            :value label}]])

(defn submit-inline [label]
  [:span.p-1
   [:input {:type "submit"
            :class "bg-clj-blue p-1.5 rounded-lg text-white w-24"
            :value label}]])

(defn button [label]
  [:span.p-1
   [:button {:type "button"
             :class "bg-clj-blue p-1.5 rounded-lg text-white w-24"}
    label]])

(defn warning [msg]
  [:span {:class "bg-red-600 p-2 rounded-lg text-white"} msg])

(defn h1 [& contents]
  [:h1.text-3xl contents])

(defn h2 [& contents]
  [:h2.text-2xl contents])

(defn h3 [& contents]
  [:h3.text-xl contents])

(defn modal [width & contents]
  [:div#modal {:class "fixed left-0 top-0 w-full h-full
  pt-6 z-10"
               :style {:background-color "rgba(0,0,0,0.4)"}
               :_ "on click if target.id === 'modal' add .hidden"}
   [:div {:class (str "mx-auto border rounded-lg bg-white " width)}
    contents]])

(defn p [& contents]
  [:p.m-3 contents])
